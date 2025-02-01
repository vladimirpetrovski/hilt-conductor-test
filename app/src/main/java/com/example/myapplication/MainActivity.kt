package com.example.myapplication

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import com.bluelinelabs.conductor.Conductor.attachRouter
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.example.myapplication.databinding.ActivityBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @Named("first")
    lateinit var first: String

    private lateinit var binding: ActivityBinding

    private lateinit var mainRouter: Router

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        Log.e("MainActivity", first)

        binding = ActivityBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)

        mainRouter = attachRouter(this, binding.controllerContainer, savedInstanceState)
        mainRouter.setRoot(RouterTransaction.with(MainController(null)))
    }
}
