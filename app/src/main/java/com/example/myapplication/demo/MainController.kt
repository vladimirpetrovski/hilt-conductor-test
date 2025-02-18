package com.example.myapplication.demo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.annotations.ConductorEntryPoint
import com.example.myapplication.demo.databinding.ControllerMainBinding
import javax.inject.Inject
import javax.inject.Named

@ConductorEntryPoint
class MainController @JvmOverloads constructor(args: Bundle? = null) : BaseController(args) {

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
        return binding.root
    }
}
