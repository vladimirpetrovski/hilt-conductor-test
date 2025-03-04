package com.example.library

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent

@EntryPoint
@InstallIn(ActivityComponent::class)
interface ControllerComponentEntryPoint {
    fun controllerComponentBuilder(): ControllerComponent.Builder
}
