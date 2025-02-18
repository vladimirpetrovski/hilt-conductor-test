package com.example.myapplication.demo

import android.content.Context
import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import com.example.library.ControllerInjector
import javax.inject.Inject
import javax.inject.Named

abstract class BaseController(args: Bundle?) : Controller(args) {

    @Inject
    @Named("first")
    lateinit var first: String

    override fun onContextAvailable(context: Context) {
        super.onContextAvailable(context)
        ControllerInjector.inject(this)
    }
}
