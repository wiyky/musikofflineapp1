// File: MainActivity.kt
package com.example.musikofflineapp

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.*
import com.example.musikofflineapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var playBtn: ImageButton
    private lateinit var pauseBtn: ImageButton
    private lateinit var nextBtn: ImageButton
    private lateinit var prevBtn: ImageButton
    private lateinit var repeatBtn: ImageButton
    private lateinit var seekBar: SeekBar
    private lateinit var songTitle: TextView

    private var mediaPlayer: MediaPlayer? = null
    private var isRepeating = false
    private var currentSongIndex = 0

    private val songs = listOf(
        R.raw.song1,
        R.raw.song2,
        R.raw.song3
    )

    private val songNames = listOf("Lagu Pertama", "Lagu Kedua", "Lagu Ketiga")

    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playBtn = findViewById(R.id.btnPlay)
        pauseBtn = findViewById(R.id.btnPause)
        nextBtn = findViewById(R.id.btnNext)
        prevBtn = findViewById(R.id.btnPrev)
        repeatBtn = findViewById(R.id.btnRepeat)
        seekBar = findViewById(R.id.seekBar)
        songTitle = findViewById(R.id.songTitle)

        playSong(currentSongIndex)

        playBtn.setOnClickListener {
            mediaPlayer?.start()
        }

        pauseBtn.setOnClickListener {
            mediaPlayer?.pause()
        }

        nextBtn.setOnClickListener {
            nextSong()
        }

        prevBtn.setOnClickListener {
            prevSong()
        }

        repeatBtn.setOnClickListener {
            isRepeating = !isRepeating
            mediaPlayer?.isLooping = isRepeating
            Toast.makeText(this, if (isRepeating) "Repeat ON" else "Repeat OFF", Toast.LENGTH_SHORT).show()
        }

        seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mediaPlayer?.seekTo(progress)
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun playSong(index: Int) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(this, songs[index])
        mediaPlayer?.start()

        songTitle.text = songNames[index]

        seekBar.max = mediaPlayer?.duration ?: 0

        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    seekBar.progress = mediaPlayer?.currentPosition ?: 0
                    handler.postDelayed(this, 500)
                } catch (e: Exception) {}
            }
        }, 0)

        mediaPlayer?.setOnCompletionListener {
            if (!isRepeating) {
                nextSong()
            }
        }
    }

    private fun nextSong() {
        currentSongIndex = (currentSongIndex + 1) % songs.size
        playSong(currentSongIndex)
    }

    private fun prevSong() {
        currentSongIndex = if (currentSongIndex - 1 < 0) songs.size - 1 else currentSongIndex - 1
        playSong(currentSongIndex)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        handler.removeCallbacksAndMessages(null)
    }
}