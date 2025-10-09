package com.letstwinkle.compose

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import kotlin.math.*

/**
 * The AnimatedCounter is a composable that displays a nonnegative integer and transitions between
 * value changes with an animation that scrolls or spins through integers from the previous to the
 * current value. With an increase in the value, the digits spin from above the counter; with a
 * decrease, the digits spin from below. When entering a composition, the initial provided value
 * is not animated.
 * @param value A nonnegative integer that is displayed similar to how a `Text` element displays
 *        integers. Hence represented by UInt.
 * @param modifier The modifier to be applied to the counter.
 * @param animationDelayMsec The desired delay from when a new value is registered to when the
 *        change should be animated; during the delay the prior value is still shown. 0 by default.
 * @param animationDurationMsec The desired duration of the animation from the prior value to the
 *        current one. The default is the default duration of Jetpack Compose animations.
 * @param digitSpacing In some font-platform combinations, numbers can look a bit squished. This
 *        Dp value adds the provided number of dp's between the digits, 0 by default.
 * @param textStyle The Text style to use for the digits. By default, a default font at 24sp.
 * @param numberOfEndDigitsThatNeverAnimate If your counter's value is always a multiple of ten or
 *        hundred, you might wish to skip animating the end digits; otherwise they could spin
 *        through a hundred values, which of course would look like a blur. For example,
 *        if this counter represents a score that's always incremented by 100, you could set this to
 *        `2`. 0 by default.
 */
@Composable
public fun AnimatedCounter(
   value: UInt,
   modifier: Modifier = Modifier,
   animationDelayMsec: Int = 0,
   animationDurationMsec: Int = AnimationConstants.DefaultDurationMillis,
   digitSpacing: Dp = 0.dp,
   textStyle: TextStyle = TextStyle(fontSize = 24.sp),
   numberOfEndDigitsThatNeverAnimate: Int = 0,
) {
   val measurer = rememberTextMeasurer(10)
   val previousValue = rememberPrevious(value)
   // normalize digit size since font could be non monospace
   val digitSize: IntSize = remember(textStyle) {
      var maxSize = IntSize.Zero
      repeat(10) { i ->
         maxSize = maxOf(
            maxSize,
            measurer.measure(i.toString(), textStyle, softWrap = false, maxLines = 1).size
         ) { a, b -> a.width.compareTo(b.width) }
      }
      maxSize
   }
   val valueString = value.toString()
   val numberOfNewDigits = valueString.length - previousValue.toString().length

   // Calculate the deltas in reverse order prior to building Row in forward order
   @Suppress("RedundantLambdaOrAnonymousFunction")
   val deltas = {
      var previousValueShifting = previousValue.toInt()
      var valueShifting = value.toInt()
      (1..valueString.length).map {
         val delta = valueShifting - previousValueShifting
         valueShifting /= 10
         previousValueShifting /= 10
         delta
      }.asReversed()
   }()
   
   Row(modifier, horizontalArrangement = Arrangement.spacedBy(digitSpacing)) {
      val dpSize = with(LocalDensity.current) { DpSize(digitSize.width.toDp(), digitSize.height.toDp()) }
      valueString.forEachIndexed { i, digitChar ->
         // keys fixed from right side, so ones stays ones, etc.
         val reverseIndex = valueString.length - i
         key(reverseIndex) {
            if (reverseIndex <= numberOfEndDigitsThatNeverAnimate) // don't ever animate; might as well use Text
               Text(
                  digitChar.toString(),
                  Modifier.size(dpSize),
                  softWrap = false,
                  style = textStyle
               )
            else AnimatedNumberDigit(
               digitChar,
               dpSize,
               digitSize.height,
               measurer,
               textStyle,
               animationDelayMsec,
               animationDurationMsec,
               deltas[i],
               i < numberOfNewDigits,
               value
            )
         }
      }
   }
}

@Composable
private fun AnimatedNumberDigit(
   finalDigit: Char,
   size: DpSize,
   heightPx: Int,
   measurer: TextMeasurer,
   style: TextStyle,
   delayMsec: Int = 0,
   durationMsec: Int,
   delta: Int = 0,
   omitLastDraw: Boolean = false,
   skipControl: UInt = 0u
) {
   val offsetAnimatable = remember(finalDigit, delta, skipControl) {
      Animatable(-delta.toFloat() * heightPx)
   }

   LaunchedEffect(offsetAnimatable) {
      offsetAnimatable.animateTo(
         0f,
         tween(durationMsec, delayMsec, CubicBezierEasing(0.42f, 0f, 0.58f, 1.0f))
      )
   }

   Box(Modifier
      .size(size)
      .clipToBounds()
      .offset { IntOffset(0, offsetAnimatable.value.roundToInt()) }
      .drawWithContent {
         // drawing the final digit first and draw <delta> prior digits below (above). the final digit has lowest negative
         // (highest positive) offset, in case of positive and negative delta, respectively.
         var digitChar = finalDigit
         var topLeft = Offset.Zero
         repeat(abs(delta) + if (omitLastDraw) 0 else 1) {
            drawTextUnbounded(measurer, digitChar.toString(), topLeft, style)
            if (delta > 0) {
               digitChar = when (digitChar) {
                  '9' -> '8'
                  '8' -> '7'
                  '7' -> '6'
                  '6' -> '5'
                  '5' -> '4'
                  '4' -> '3'
                  '3' -> '2'
                  '2' -> '1'
                  '1' -> '0'
                  '0' -> '9'
                  else -> error("Impossible")
               }
            } else {
               digitChar = when (digitChar) {
                  '9' -> '0'
                  '8' -> '9'
                  '7' -> '8'
                  '6' -> '7'
                  '5' -> '6'
                  '4' -> '5'
                  '3' -> '4'
                  '2' -> '3'
                  '1' -> '2'
                  '0' -> '1'
                  else -> error("Impossible")
               }
            }
            topLeft += Offset(0f, delta.sign * heightPx.toFloat())
         }
      }
   )
}

/**
 * Returns a dummy MutableState that does not cause render when setting it
 */
@Composable
private fun <T> rememberRef(initialValue: T): MutableState<T> {
   return remember {
      object: MutableState<T> {
         override var value: T = initialValue
         override fun component1(): T = value
         override fun component2(): (T) -> Unit = { value = it }
      }
   }
}
@Composable
private fun <T> rememberPrevious(
   current: T,
   shouldUpdate: (prev: T?, curr: T) -> Boolean = { a: T?, b: T -> a != b },
): T {
   val ref = rememberRef(current)
   
   // launched after render, so the current render will have the old value anyway
   SideEffect {
      if (shouldUpdate(ref.value, current)) {
         ref.value = current
      }
   }
   
   return ref.value
}

private fun DrawScope.drawTextUnbounded(
   textMeasurer: TextMeasurer,
   text: String,
   topLeft: Offset = Offset.Zero,
   style: TextStyle = TextStyle.Default,
) {
   val textLayoutResult = textMeasurer.measure(text, style, TextOverflow.Clip, false, 1)
   
   withTransform({
      translate(topLeft.x, topLeft.y)
   }) {
      textLayoutResult.multiParagraph.paint(drawContext.canvas, blendMode = DrawScope.DefaultBlendMode)
   }
}