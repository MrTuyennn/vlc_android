package com.example.vlcjuncook

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.vlcjuncook.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var libVLC: LibVLC? = null
    private var vlcMediaPlayer: MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.loadingVideo.visibility = View.VISIBLE
        libVLC = LibVLC(
            this, arrayListOf(
                "--file-caching=150",
                "--network-caching=150",
                "--clock-jitter=0",
                "--live-caching=150",
                "--drop-late-frames",
                "--skip-frames",
                "--vout=android-display",
                "--sout-transcode-vb=20",
                "--no-audio",
                "--sout=#transcode{vcodec=h264,vb=20,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display}",
                "--sout-x264-nf"
            )
        )
        initializeVlcVideoPlayer("rtsp://admin:1234@goduck.dvrhost.net:554/video1");
        // Initialize your VLC player here
    }

    override fun onPause() {
        super.onPause()
        // Pause video playback if the activity is paused
        vlcMediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        // Resume video playback if the activity is resumed
        vlcMediaPlayer?.play()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources
        releaseVlcPlayer()
    }


    private fun releaseVlcPlayer() {
        vlcMediaPlayer?.stop()
        vlcMediaPlayer?.detachViews()
        vlcMediaPlayer?.release()
        libVLC?.release()
        vlcMediaPlayer = null
        libVLC = null
    }

    fun clearVlcPlayerMedia() {
        vlcMediaPlayer?.stop()
        vlcMediaPlayer?.media?.release()
    }


    private fun setVlcMedia(videoUrl: String) {
        val media = Media(libVLC, Uri.parse(videoUrl))
        vlcMediaPlayer?.media = media
        vlcMediaPlayer?.play()
        media.release() // Release the media object once it is attached to the player
    }

    private fun initializeVlcVideoPlayer(videoUrl: String) {
        videoUrl?.let { url ->
            val media = Media(libVLC, Uri.parse(videoUrl))
            vlcMediaPlayer = MediaPlayer(libVLC)
            vlcMediaPlayer?.media = media
            vlcMediaPlayer?.attachViews(binding.vlcVideoLayout, null, false, false)
            vlcMediaPlayer?.setEventListener { event ->
                handleVlcEvents(event)
            }
            vlcMediaPlayer?.play()
            media.release()
            Log.d(TAG, "VLC Event Playing")
        }
    }


    private fun handleVlcEvents(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.Opening, MediaPlayer.Event.Buffering -> {
                binding.loadingVideo.visibility = View.VISIBLE
                Log.d(TAG, "VLC Event Opening/Buffering")
            }

            MediaPlayer.Event.Playing -> {
                binding.loadingVideo.visibility = View.INVISIBLE
                Log.d(TAG, "VLC Event Playing")
            }

            MediaPlayer.Event.Paused -> {
                Log.d(TAG, "VLC Event Paused")
            }

            MediaPlayer.Event.Stopped -> {
                binding.loadingVideo.visibility = View.INVISIBLE
                CoroutineScope(Dispatchers.Main).launch {
                    delay(500)
                    setVlcMedia("rtsp://admin:1234@goduck.dvrhost.net:554/video1")
                }
                Log.d(TAG, "VLC Event Stopped")
            }

            MediaPlayer.Event.EncounteredError -> {
                binding.loadingVideo.visibility = View.INVISIBLE
                Log.d(TAG, "VLC Event Error")
            }

            MediaPlayer.Event.EndReached -> {
                binding.loadingVideo.visibility = View.INVISIBLE
                Log.d(TAG, "VLC Event End Reached")
            }

            MediaPlayer.Event.TimeChanged -> {
             //   Log.d(TAG, "VLC Event Time Changed")
            }

            MediaPlayer.Event.PositionChanged -> {
              //  Log.d(TAG, "VLC Event Position Changed")
            }

            MediaPlayer.Event.SeekableChanged -> {
                Log.d(TAG, "VLC Event Seekable Changed")
            }

            MediaPlayer.Event.PausableChanged -> {
                Log.d(TAG, "VLC Event Pausable Changed")
            }

            MediaPlayer.Event.LengthChanged -> {
                Log.d(TAG, "VLC Event Length Changed")
            }

            MediaPlayer.Event.Vout -> {
                binding.loadingVideo.visibility = View.INVISIBLE
                Log.d(TAG, "VLC Event Video Output")
            }

            MediaPlayer.Event.ESAdded -> {
                Log.d(TAG, "VLC Event Elementary Stream Added")
            }

            MediaPlayer.Event.ESDeleted -> {
                Log.d(TAG, "VLC Event Elementary Stream Deleted")
            }

            MediaPlayer.Event.ESSelected -> {
                Log.d(TAG, "VLC Event Elementary Stream Selected")
            }

            else -> {
                Log.d(TAG, "VLC Event Other: ${event.type}")
            }
        }
    }


}