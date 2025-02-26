package com.example.myapplication.demo

import androidx.lifecycle.ViewModel
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel

@HiltViewModel(assistedFactory = ProfileViewModel.Factory::class)
class ProfileViewModel @AssistedInject constructor(
    @Assisted val runtimeArg: String,
    private val repository: UserRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(runtimeArg: String): ProfileViewModel
    }

    fun printUser() {
        println("Entered ProfileViewModel")
    }
}
