package com.example.library

import com.bluelinelabs.conductor.Controller
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.internal.GeneratedComponentManager

class ControllerComponentManager(
    private val controller: Controller
) : GeneratedComponentManager<ControllerComponent> {

    private var _controllerComponent: ControllerComponent? = null

    private val controllerComponentLock: Any = Any()

    override fun generatedComponent(): ControllerComponent {
        if (_controllerComponent == null) {
            synchronized(controllerComponentLock) {
                if (_controllerComponent == null) {
                    _controllerComponent = createControllerComponent()
                }
            }
        }
        return _controllerComponent!!
    }

    private fun createControllerComponent(): ControllerComponent {
        val activity = controller.activity
            ?: throw IllegalStateException("Controller must be attached to an activity")

        val controllerComponentBuilder =
            EntryPointAccessors.fromActivity(
                activity,
                ControllerComponentEntryPoint::class.java
            )

        return controllerComponentBuilder.controllerComponentBuilder()
            .activity(activity)
            .controller(controller)
            .build()
    }
}