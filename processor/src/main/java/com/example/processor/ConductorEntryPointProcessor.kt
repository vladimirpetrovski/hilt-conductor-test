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

class ConductorEntryPointProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(ConductorEntryPoint::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }.toList()

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it.accept(ConductorEntryPointInterfaceVisitor(), Unit)
                it.accept(InjectionUtilVisitor(), Unit)
            }

        if (symbols.count() == 0) {
            logger.warn("No symbols found with @ConductorEntryPoint annotation")
        } else {
            logger.info("Processing ${symbols.count()} symbols with @ConductorEntryPoint annotation")
        }

        return ret
    }

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
                                    ClassName("com.example.library", "ControllerComponent")
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
                            FunSpec.builder("inject${className}")
                                .addParameter(
                                    className.decapitalize(),
                                    classDeclaration.toClassName()
                                )
                                .build()
                        )
                        .build()
                )
                .build()

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

    inner class InjectionUtilVisitor : KSVisitorVoid() {
        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            logger.info("Generating InjectionUtil for ${classDeclaration.qualifiedName?.asString()}")
            val packageName = classDeclaration.packageName.asString()
            val className = classDeclaration.simpleName.asString()
            val generatedUtilName = "${className}HiltInjection"

            val fileSpec = FileSpec.builder(packageName, generatedUtilName)
                .addImport("com.example.library", "ControllerComponentManager")
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
                                        .returns(ClassName("com.example.library", "ControllerComponentManager"))
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

class ConductorEntryPointProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return ConductorEntryPointProcessor(environment.codeGenerator, environment.logger)
    }
}
