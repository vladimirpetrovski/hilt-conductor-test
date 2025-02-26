package com.example.library

import androidx.annotation.MainThread
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.viewmodel.CreationExtras

/**
 * Returns a [Lazy] delegate to access the BaseController's ViewModel, if [factoryProducer] is
 * specified then [ViewModelProvider.Factory] returned by it will be used to create [ViewModel]
 * first time.
 *
 * ```
 * class MyComponentController : BaseController() {
 *     val viewmodel: MyViewModel by viewModels()
 * }
 * ```
 *
 * This property can be accessed only after the Controller is attached to the Application, and access
 * prior to that will result in IllegalArgumentException.
 */
@MainThread
inline fun <reified VM : ViewModel> ConductorController.viewModels(
    noinline extrasProducer: (() -> CreationExtras)? = null,
    noinline factoryProducer: (() -> Factory)? = null
): Lazy<VM> {
    val factoryPromise = factoryProducer ?: { defaultViewModelProviderFactory }

    return ViewModelLazy(
        VM::class,
        { viewModelStore },
        factoryPromise,
        { extrasProducer?.invoke() ?: this.defaultViewModelCreationExtras }
    )
}
