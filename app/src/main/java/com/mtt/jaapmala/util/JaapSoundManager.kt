package com.mtt.jaapmala.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Build
import androidx.annotation.RawRes
import com.mtt.jaapmala.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JaapSoundManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    // -------------------------
    // Audio Focus
    // -------------------------

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private var audioFocusRequest: AudioFocusRequest? = null

    // -------------------------
    // SoundPool (Tick + Bell)
    // -------------------------

    private val soundPool: SoundPool
    private var tickSoundId: Int = 0
    private var bellSoundId: Int = 0
    private var soundsLoaded = false

    // -------------------------
    // Meditation Player
    // -------------------------

    private var meditationPlayer: MediaPlayer? = null

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(attributes)
            .build()

        loadSounds()
    }

    // -------------------------
    // Load Short Sounds
    // -------------------------

    private fun loadSounds() {
        tickSoundId = soundPool.load(context, R.raw.tick, 1)
        bellSoundId = soundPool.load(context, R.raw.bell, 1)

        soundPool.setOnLoadCompleteListener { _, _, _ ->
            soundsLoaded = true
        }
    }

    // -------------------------
    // Tick Sound
    // -------------------------

    fun playJaapTick() {
        if (!soundsLoaded) return

        soundPool.play(
            tickSoundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    // -------------------------
    // Mala Bell
    // -------------------------

    fun playMalaBell() {
        if (!soundsLoaded) return

        soundPool.play(
            bellSoundId,
            1f,
            1f,
            1,
            0,
            1f
        )
    }

    // -------------------------
    // Meditation Background
    // -------------------------

    fun startMeditation(@RawRes soundRes: Int) {

        if (meditationPlayer != null) return

        requestAudioFocus()

        meditationPlayer = MediaPlayer.create(context, soundRes).apply {
            isLooping = true
            setVolume(0.6f, 0.6f)
            start()
        }
    }

    fun stopMeditation() {
        meditationPlayer?.apply {
            stop()
            release()
        }
        meditationPlayer = null
        abandonAudioFocus()
    }

    // -------------------------
    // Audio Focus Handling
    // -------------------------

    private fun requestAudioFocus() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val focusRequest = AudioFocusRequest.Builder(
                AudioManager.AUDIOFOCUS_GAIN
            )
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setOnAudioFocusChangeListener { focusChange ->
                    when (focusChange) {
                        AudioManager.AUDIOFOCUS_LOSS -> stopMeditation()
                        AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ->
                            meditationPlayer?.pause()
                        AudioManager.AUDIOFOCUS_GAIN ->
                            meditationPlayer?.start()
                    }
                }
                .build()

            audioManager.requestAudioFocus(focusRequest)
            audioFocusRequest = focusRequest
        }
    }

    private fun abandonAudioFocus() {
        audioFocusRequest?.let {
            audioManager.abandonAudioFocusRequest(it)
        }
    }

    // -------------------------
    // Cleanup
    // -------------------------

    fun release() {
        soundPool.release()
        stopMeditation()
    }
}
