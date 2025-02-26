package com.example.library

import androidx.lifecycle.ViewModelProvider
import com.bluelinelabs.conductor.Controller
import com.example.library.DefaultViewModelFactories.InternalFactoryFactory
import dagger.hilt.EntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory
import dagger.hilt.android.internal.lifecycle.HiltViewModelMap
import javax.inject.Inject

object DefaultViewModelFactories {

    fun getControllerFactory(
        controller: Controller,
        delegateFactory: ViewModelProvider.Factory
    ): ViewModelProvider.Factory {
        return EntryPoints.get(controller, ControllerEntryPoint::class.java)
            .hiltInternalFactoryFactory
            .getHiltViewModelFactory(delegateFactory)
    }

    class InternalFactoryFactory @Inject constructor(
        @HiltViewModelMap.KeySet private val keySet: Map<Class<*>, Boolean>,
        private val viewModelComponentBuilder: ViewModelComponentBuilder
    ) {
        fun getHiltViewModelFactory(
            delegate: ViewModelProvider.Factory
        ): ViewModelProvider.Factory {
            return HiltViewModelFactory(keySet, checkNotNull(delegate), viewModelComponentBuilder)
        }
    }

    @EntryPoint
    @InstallIn(ControllerComponent::class)
    interface ControllerEntryPoint {
        val hiltInternalFactoryFactory: InternalFactoryFactory
    }
}
