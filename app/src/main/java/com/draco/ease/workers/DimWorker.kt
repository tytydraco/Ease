package com.draco.ease.workers

import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.android.material.math.MathUtils
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class DimWorker(private val context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val currentBrightness = getBrightness()
        val newBrightness = currentBrightness * 0.25

        val endTime = TimeUnit.MINUTES.toMillis(5)
        val step = 500L

        for (i in 0L until endTime step step) {
            val amount = i / endTime.toFloat()
            val curVal = MathUtils.lerp(
                currentBrightness.toFloat(),
                newBrightness.toFloat(),
                amount
            ).roundToInt()

            Log.d("P", (amount * 100).toString())
            Log.d("NEW", curVal.toString())
            setBrightness(curVal)

            delay(step)
        }

        return Result.success()
    }

    private fun setBrightness(value: Int) {
        Settings.System.putInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            value
        )
    }

    private fun getBrightness(): Int {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            50
        )
    }
}