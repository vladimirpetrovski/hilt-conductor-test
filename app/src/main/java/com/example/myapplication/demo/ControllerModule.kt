package com.example.myapplication.demo

import com.example.annotations.ControllerScoped
import com.example.library.ControllerComponent
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
    fun textOne(): String = "First"

    @Provides
    @ControllerScoped
    @Named("second")
    fun textTwo(): String {
        return "Second"
    }
}
