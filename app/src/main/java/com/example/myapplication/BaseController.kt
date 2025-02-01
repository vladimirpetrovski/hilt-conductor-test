package com.example.myapplication

import android.os.Bundle
import com.bluelinelabs.conductor.Controller
import javax.inject.Inject
import javax.inject.Named

abstract class BaseController(args: Bundle?) : Controller(args) {

    @Inject
    @Named("first")
    lateinit var first: String
}