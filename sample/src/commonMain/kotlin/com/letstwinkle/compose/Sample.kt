package com.letstwinkle.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.max

@Composable
fun Sample() {
    var value by remember { mutableIntStateOf(100) }
    val valueU = remember { derivedStateOf { value.toUInt() } }
    var delta by remember { mutableIntStateOf(1) }
    var numberOfMinorDigitsThatNeverAnimate by remember { mutableIntStateOf(0) }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("cmp-animatedcounter", fontSize = 18.sp)
        AnimatedCounter(
            valueU.value,
            Modifier.padding(vertical = 12.dp),
            textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.SemiBold),
            animationDurationMsec = 533,
            numberOfEndDigitsThatNeverAnimate = numberOfMinorDigitsThatNeverAnimate
        )
        TextField(
            value = delta.toString(),
            onValueChange = {
                try {
                    delta = it.toInt()
                } catch (ex: Throwable) {}
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.widthIn(max = 100.dp)
        )
        Row(Modifier.padding(vertical = 16.dp), Arrangement.spacedBy(8.dp)) {
            Button({ value += delta }) {
                Text("UP")
            }
            Button({ value = max(0, value - delta) }) {
                Text("DOWN")
            }
        }
        Row(Modifier.padding(vertical = 16.dp)) {
            Text("numberOfMinorDigitsThatNeverAnimate")
            TextField(
                value = numberOfMinorDigitsThatNeverAnimate.toString(),
                onValueChange = {
                    try {
                        numberOfMinorDigitsThatNeverAnimate = it.toInt()
                    } catch (ex: Throwable) {}
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            )
        }
    }
}
