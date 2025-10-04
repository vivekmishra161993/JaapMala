package com.mtt.jaapmala.data

import android.content.Context
import android.media.MediaPlayer
import androidx.annotation.RawRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class MeditationSoundManager @Inject constructor(
    private val context: Context
) {
    private var mediaPlayer: MediaPlayer? = null
    private var fadeJob: Job? = null

    fun playSound(@RawRes resId: Int) {
        stopSound() // stop any previous sound
        mediaPlayer = MediaPlayer.create(context, resId).apply {
            isLooping = true
            setVolume(1f, 1f)
            start()
        }
    }

    fun stopSound() {
        fadeJob?.cancel()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    fun fadeOutAndStop(durationMs: Long = 500) {
        val player = mediaPlayer ?: return
        fadeJob?.cancel()
        fadeJob = CoroutineScope(Dispatchers.Main).launch {
            val steps = 20
            val delayPerStep = durationMs / steps
            for (i in steps downTo 0) {
                val volume = i / steps.toFloat()
                player.setVolume(volume, volume)
                delay(delayPerStep)
            }
            stopSound()
        }
    }
}

