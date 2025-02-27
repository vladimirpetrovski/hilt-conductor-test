package com.example.library

import com.bluelinelabs.conductor.Controller

object ControllerInjector {
    fun inject(controller: Controller): ControllerComponentManager {
        val className = controller::class.java.name + "HiltInjection"

        try {
            // Load the class dynamically
            val clazz = Class.forName(
                className,
                true,
                controller.javaClass.classLoader
            )

            // Look for the static inject function directly in the class
            val method = clazz.getDeclaredMethod("inject", Controller::class.java)

            // Invoke static inject function with the controller parameter
            return method.invoke(null, controller) as ControllerComponentManager
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
