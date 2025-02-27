package com.example.processor

import com.example.annotations.ConductorEntryPoint
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName

/**
 * Symbol Processor for handling @ConductorEntryPoint annotations.
 * This processor generates two key components:
 * 1. **Generated Injector Interfaces** (`*_GeneratedInjector`) for annotated controllers.
 * 2. **Injection Utility Classes** (`*HiltInjection`) to handle dependency injection dynamically.
 *
 * **Purpose:**
 * - Integrates Hilt with Conductor by generating necessary components.
 * - Ensures controllers annotated with `@ConductorEntryPoint` can be injected properly.
 * - Uses **KSP (Kotlin Symbol Processing API)** to generate Kotlin code during compilation.
 *
 * **Reference Implementation:**
 * - Inspired by [Hilt's Dagger Integration](https://dagger.dev/hilt/)
 */
class ConductorEntryPointProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    /**
     * Processes symbols annotated with `@ConductorEntryPoint`.
     * - Validates symbols.
     * - Generates necessary injector interfaces and injection utility classes.
     *
     * @param resolver Used to find annotated elements in the codebase.
     * @return List of invalid symbols (if any).
     */
    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(ConductorEntryPoint::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }.toList()

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it.accept(ConductorEntryPointInterfaceVisitor(), Unit)
                it.accept(InjectionUtilVisitor(), Unit)
            }

        // Log whether any symbols were found for debugging and maintenance
        if (symbols.count() == 0) {
            logger.warn("No symbols found with @ConductorEntryPoint annotation")
        } else {
            logger.info("Processing ${symbols.count()} symbols with @ConductorEntryPoint annotation")
        }

        return ret
    }

    /**
     * Visitor that generates an injector interface for each annotated controller.
     *
     * **Example Output:**
     * ```kotlin
     * interface MyController_GeneratedInjector {
     *     fun injectMyController(controller: MyController)
     * }
     * ```
     */
    inner class ConductorEntryPointInterfaceVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.info("Visiting class: ${classDeclaration.qualifiedName?.asString()}")
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()
            val generatedInterfaceName = "${className}_GeneratedInjector"

            val fileSpec = FileSpec.builder(packageName, generatedInterfaceName)
                .addType(
                    TypeSpec.interfaceBuilder(generatedInterfaceName)
                        .addAnnotation(
                            AnnotationSpec.builder(
                                ClassName(
                                    "dagger.hilt.codegen",
                                    "OriginatingElement"
                                )
                            )
                                .addMember(
                                    "topLevelClass = %T::class",
                                    classDeclaration.toClassName()
                                )
                                .build()
                        )
                        .addAnnotation(ClassName("dagger.hilt.internal", "GeneratedEntryPoint"))
                        .addAnnotation(
                            AnnotationSpec.builder(ClassName("dagger.hilt", "InstallIn"))
                                .addMember(
                                    "%T::class",
                                    ClassName("com.selabs.speak.library.conductor", "ControllerComponent")
                                )
                                .build()
                        )
                        .addAnnotation(
                            AnnotationSpec.builder(
                                ClassName(
                                    "javax.annotation.processing",
                                    "Generated"
                                )
                            )
                                .addMember(
                                    "value = [%S]",
                                    "dagger.hilt.android.processor.internal.androidentrypoint.InjectorEntryPointGenerator"
                                )
                                .build()
                        )
                        .addFunction(
                            FunSpec.builder("inject$className")
                                .addParameter(
                                    className.decapitalize(),
                                    classDeclaration.toClassName()
                                )
                                .build()
                        )
                        .build()
                )
                .build()

            // Generate file and write output
            val file = codeGenerator.createNewFile(
                Dependencies(true, classDeclaration.containingFile!!),
                packageName,
                generatedInterfaceName
            )

            file.writer().use { writer ->
                fileSpec.writeTo(writer)
            }

            logger.info("Generated file for $className at $packageName.$generatedInterfaceName")
        }
    }

    /**
     * Visitor that generates a utility class to handle dependency injection.
     *
     * **Example Output:**
     * ```kotlin
     * class MyControllerHiltInjection {
     *     companion object {
     *         @JvmStatic
     *         fun inject(controller: MyController): ControllerComponentManager { ... }
     *     }
     * }
     * ```
     */
    inner class InjectionUtilVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.info("Generating InjectionUtil for ${classDeclaration.qualifiedName?.asString()}")
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()
            val generatedUtilName = "${className}HiltInjection"

            val fileSpec = FileSpec.builder(packageName, generatedUtilName)
                .addImport("com.selabs.speak.library.conductor", "ControllerComponentManager")
                .addType(
                    TypeSpec.classBuilder(generatedUtilName)
                        .addType(
                            TypeSpec.companionObjectBuilder()
                                .addFunction(
                                    FunSpec.builder("inject")
                                        .addAnnotation(JvmStatic::class)
                                        .addParameter(
                                            "controller",
                                            ClassName("com.bluelinelabs.conductor", "Controller")
                                        )
                                        .returns(ClassName("com.selabs.speak.library.conductor", "ControllerComponentManager"))
                                        .addCode(
                                            """
                                            val componentManager = ControllerComponentManager(controller)
                                            val generatedComponent = componentManager.generatedComponent()
                                            (generatedComponent as ${className}_GeneratedInjector).inject$className(controller as $className)
                                            return componentManager
                                            """.trimIndent()
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .build()
                )
                .build()

            // Generate file and write output
            val file = codeGenerator.createNewFile(
                Dependencies(true, classDeclaration.containingFile!!),
                packageName,
                generatedUtilName
            )

            file.writer().use { writer ->
                fileSpec.writeTo(writer)
            }

            logger.info("Generated $generatedUtilName for $className at $packageName.$generatedUtilName")
        }
    }
}

/**
 * Provides the `ConductorEntryPointProcessor` to the KSP environment.
 * This class is required for the KSP framework to recognize and instantiate the processor.
 */
class ConductorEntryPointProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ConductorEntryPointProcessor(environment.codeGenerator, environment.logger)
    }
}
