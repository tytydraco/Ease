package com.draco.ease.workers

import android.content.Context
import android.provider.Settings
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.draco.ease.R
import com.google.android.material.math.MathUtils
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class DimWorker(private val context: Context, workerParams: WorkerParameters): CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        val ratio = inputData.getFloat(context.getString(R.string.pref_ratio_key), 0.5f)
        val duration = inputData.getInt(context.getString(R.string.pref_duration_key), 5)

        val currentBrightness = getBrightness()
        val newBrightness = currentBrightness * ratio

        val endTime = TimeUnit.MINUTES.toMillis(duration.toLong())
        val step = 500L

        for (i in 0L until endTime step step) {
            val amount = i / endTime.toFloat()
            val curVal = MathUtils.lerp(
                currentBrightness.toFloat(),
                newBrightness,
                amount
            ).roundToInt()

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