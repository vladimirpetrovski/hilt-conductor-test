package com.example.myapplication.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.annotations.ConductorEntryPoint
import com.example.myapplication.demo.databinding.ControllerMainBinding
import com.example.library.viewModels
import dagger.hilt.android.lifecycle.withCreationCallback
import javax.inject.Inject
import javax.inject.Named

@ConductorEntryPoint
class ProfileController @JvmOverloads constructor(args: Bundle? = null) : BaseController(args) {

    private val viewModel: ProfileViewModel by viewModels<ProfileViewModel>(
        extrasProducer = {
            defaultViewModelCreationExtras.withCreationCallback<ProfileViewModel.Factory> { factory ->
                factory.create(runtimeArg = "abc")
            }
        }
    )

    @Inject
    @Named("second")
    lateinit var second: String

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val binding = ControllerMainBinding.inflate(inflater, container, false)
        binding.tvTest.text = "$first $second"
        viewModel.printUser()
        return binding.root
    }
}
