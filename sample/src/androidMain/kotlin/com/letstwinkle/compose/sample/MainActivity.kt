package com.letstwinkle.compose.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.letstwinkle.compose.Sample
import com.letstwinkle.compose.sample.ui.theme.CmpanimatedcounterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CmpanimatedcounterTheme {
                Sample()
            }
        }
    }
}
