package com.example.myapplication

import com.funnydevs.hilt_conductor.ControllerComponent
import com.funnydevs.hilt_conductor.annotations.ControllerScoped
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import javax.inject.Named

@Module
@InstallIn(ControllerComponent::class)
object ControllerModule {
    @Provides
    @ControllerScoped
    @Named("first")
    fun textOne(): String = "Hello World"

    @Provides
    @ControllerScoped
    @Named("second")
    fun textTwo(): String {
        return "Hello Moon"
    }
}