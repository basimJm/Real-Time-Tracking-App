package com.digitalcash.soarapoc.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.digitalcash.soarapoc.core.theme.SoaraPOCTheme
import com.digitalcash.soarapoc.presentation.map.MapScreen
import com.google.android.gms.maps.MapsInitializer
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        MapsInitializer.initialize(applicationContext)
        setContent {
            SoaraPOCTheme {
                MapScreen()
            }
        }
    }
}