package com.example.library

import android.app.Activity
import com.bluelinelabs.conductor.Controller
import com.example.annotations.ControllerScoped
import dagger.BindsInstance
import dagger.hilt.DefineComponent
import dagger.hilt.android.components.ActivityComponent

@ControllerScoped
@DefineComponent(parent = ActivityComponent::class)
interface ControllerComponent {

    @DefineComponent.Builder
    interface Builder {
        fun activity(@BindsInstance activity: Activity): Builder
        fun controller(@BindsInstance controller: Controller): Builder
        fun build(): ControllerComponent
    }
}
