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
        val controllerComponentBuilder =
            EntryPointAccessors.fromActivity(
                controller.activity!!,
                ControllerComponentEntryPoint::class.java
            )

        return controllerComponentBuilder.controllerComponentBuilder()
            .activity(controller.activity!!)
            .controller(controller)
            .build()
    }
}