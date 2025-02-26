package com.example.library

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.bluelinelabs.conductor.Controller
import dagger.hilt.internal.GeneratedComponentManagerHolder

/**
 * ConductorController is an abstract class that extends the Conductor library's Controller class.
 * It integrates with Android's architecture components to provide ViewModel support, saved state handling,
 * and dependency injection using Dagger Hilt.
 *
 * This class ensures that the controller can manage its own lifecycle, ViewModel store, and saved state registry,
 * making it easier to handle configuration changes and process death.
 *
 * @param args The arguments to be passed to the controller.
 */
abstract class ConductorController(
    private val args: Bundle?
) : Controller(args),
    ViewModelStoreOwner,
    HasDefaultViewModelProviderFactory,
    GeneratedComponentManagerHolder,
    SavedStateRegistryOwner {

    private var _controllerComponentManager: ControllerComponentManager? = null

    private val savedStateController = SavedStateRegistryController.create(this)

    init {
        savedStateController.performAttach()
        enableSavedStateHandles()
    }

    override fun onContextAvailable(context: Context) {
        savedStateController.performRestore(args)
        super.onContextAvailable(context)
        _controllerComponentManager = ControllerInjector.inject(this)
    }

    override val lifecycle: Lifecycle get() = lifecycleOwner.lifecycle

    override val viewModelStore: ViewModelStore by lazy { ViewModelStore() }

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory by lazy {
        val activity = activity ?: throw IllegalStateException("Activity is null")
        DefaultViewModelFactories.getControllerFactory(
            this,
            SavedStateViewModelFactory(activity.application, this, args)
        )
    }

    override val defaultViewModelCreationExtras: CreationExtras
        get() {
            val extras = MutableCreationExtras()

            val activity = activity
            if (activity != null) {
                extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] =
                    activity.application
            }

            extras[SAVED_STATE_REGISTRY_OWNER_KEY] = this
            extras[VIEW_MODEL_STORE_OWNER_KEY] = this

            val savedArgs = args
            if (savedArgs != null) {
                extras[DEFAULT_ARGS_KEY] = savedArgs
            }

            return extras
        }

    override fun componentManager(): ControllerComponentManager = _controllerComponentManager!!

    override fun generatedComponent(): ControllerComponent = componentManager().generatedComponent()

    override val savedStateRegistry: SavedStateRegistry get() = savedStateController.savedStateRegistry
}
