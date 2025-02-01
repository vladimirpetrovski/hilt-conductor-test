package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.MainBinding
import com.funnydevs.hilt_conductor.annotations.ConductorEntryPoint
import javax.inject.Inject
import javax.inject.Named

@ConductorEntryPoint
class MainController(args: Bundle?) : BaseController(args) {

    @Inject
    @Named("second")
    lateinit var second: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup,
        savedViewState: Bundle?
    ): View {
        val binding = MainBinding.inflate(inflater, container, false)
        Log.e("MainController", "$first AND $second")
        return binding.root
    }
}
