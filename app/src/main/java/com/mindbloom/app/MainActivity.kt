package com.mindbloom.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mindbloom.app.di.AppContainer
import com.mindbloom.app.ui.navigation.MindBloomApp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val container = AppContainer.get(applicationContext)
        setContent {
            MindBloomApp(container = container)
        }
    }
}
