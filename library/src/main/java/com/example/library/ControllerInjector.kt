package com.example.library

import com.bluelinelabs.conductor.Controller

@Suppress("MaximumLineLength")
object ControllerInjector {
    fun inject(controller: Controller): ControllerComponentManager {
        val className = controller::class.java.simpleName + "HiltInjection"
        val packageName = controller::class.java.`package`.name
        println("Looking for class: $packageName.$className") // Debugging

        try {
            // Load the class dynamically
            val clazz = Class.forName(
                "$packageName.$className",
                true,
                controller.javaClass.classLoader
            )
            println("Found class: ${clazz.name}")

            // Retrieve the Companion object using Java reflection
            val companionObject = clazz.getDeclaredField("Companion").get(null)
                ?: throw IllegalStateException("No companion object found in $className")

            // Look for the static inject function in the companion object
            val method =
                companionObject::class.java.getDeclaredMethod("inject", Controller::class.java)

            // Invoke static inject function with the controller parameter
            return method.invoke(companionObject, controller) as ControllerComponentManager
        } catch (@Suppress("TooGenericExceptionCaught") e: Exception) {
            e.printStackTrace()
            @Suppress("TooGenericExceptionThrown")
            throw RuntimeException(
                "Failed to inject dependencies for ${controller::class.java.simpleName}",
                e
            )
        }
    }
}
