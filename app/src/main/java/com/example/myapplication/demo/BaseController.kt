package com.example.myapplication.demo

import android.os.Bundle
import com.example.library.ConductorController
import javax.inject.Inject
import javax.inject.Named

abstract class BaseController(args: Bundle?) : ConductorController(args) {

    @Inject
    @Named("first")
    lateinit var first: String
}
