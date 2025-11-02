package com.mtt.jaapmala.data

import android.Manifest
import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.annotation.RawRes
import androidx.annotation.RequiresPermission
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SoundManager(
    private val context: Context
) {
    private var meditationPlayer: MediaPlayer? = null
    private var fadeJob: Job? = null
    private var isPaused = false

    private val meditationAttrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_MEDIA)
        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
        .build()

    private val bellAttrs = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()

    fun playMeditationSound(@RawRes resId: Int) {
        stopMeditationSound() // Stop previous if running
        meditationPlayer = MediaPlayer.create(context, resId).apply {
            setAudioAttributes(meditationAttrs)
            isLooping = true
            setVolume(1f, 1f)
            start()
            isPaused = false
        }
    }

    fun pauseMeditationSound() {
        meditationPlayer?.let {
            if (it.isPlaying) {
                it.pause()
                isPaused = true
            }
        }
    }

    fun resumeMeditationSound() {
        meditationPlayer?.let {
            if (isPaused) {
                it.start()
                isPaused = false
            }
        }
    }

    fun fadeOutAndStopMeditation(durationMs: Long = 500) {
        val player = meditationPlayer ?: return
        fadeJob?.cancel()
        fadeJob = CoroutineScope(Dispatchers.Main).launch {
            val steps = 20
            val delayPerStep = durationMs / steps
            for (i in steps downTo 0) {
                val volume = i / steps.toFloat()
                player.setVolume(volume, volume)
                delay(delayPerStep)
            }
            stopMeditationSound()
        }
    }

    fun stopMeditationSound() {
        fadeJob?.cancel()
        meditationPlayer?.stop()
        meditationPlayer?.release()
        meditationPlayer = null
        isPaused = false
    }

    // ---------------------------------
    // 🔔 Mala Completion Sound (short)
    // ---------------------------------
    @RequiresPermission(Manifest.permission.VIBRATE)
    fun triggerMalaCompletionFeedback(@RawRes bellResId: Int) {
        vibrate()
        playBellSound(bellResId)
    }

    private fun playBellSound(@RawRes resId: Int) {
        val uri = "android.resource://${context.packageName}/$resId".toUri()
        MediaPlayer().apply {
            setAudioAttributes(bellAttrs)
            setDataSource(context, uri)
            setOnCompletionListener { it.release() }
            prepare()
            start()
        }
    }

    private fun vibrate() {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(
            VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
        )
    }

    // ---------------------------------
    // 🧘 Lifecycle-safe helpers
    // ---------------------------------
    fun onAppBackgrounded() {
        pauseMeditationSound()
    }

    fun onAppForegrounded() {
        resumeMeditationSound()
    }

    fun releaseAll() {
        fadeJob?.cancel()
        meditationPlayer?.release()
        meditationPlayer = null
    }
}
