package com.example.vlcjuncook

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.vlcjuncook.databinding.ActivityMainBinding
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    private var libVLC: LibVLC? = null
    private var vlcMediaPlayer: org.videolan.libvlc.MediaPlayer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        libVLC = LibVLC(this, arrayListOf( "--file-caching=150",
            "--network-caching=150",
            "--clock-jitter=0",
            "--live-caching=150",
            "--drop-late-frames",
            "--skip-frames",
            "--vout=android-display",
            "--sout-transcode-vb=20",
            "--no-audio",
            "--sout=#transcode{vcodec=h264,vb=20,acodec=mpga,ab=128,channels=2,samplerate=44100}:duplicate{dst=display}",
            "--sout-x264-nf"))
        initializeVlcVideoPlayer("rtsp://admin:L2C5A6F7@172.16.1.105:554/cam/realmonitor?channel=2&subtype=0");
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
        val media = org.videolan.libvlc.Media(libVLC, Uri.parse(videoUrl))
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
            binding.vlcVideoLayout.visibility =  android.view.View.VISIBLE
            Log.d(TAG, "VLC Event Playing")
        }
    }


    private fun handleVlcEvents(event: MediaPlayer.Event) {
        when (event.type) {
            MediaPlayer.Event.Playing -> Log.d("VLC", "Playing")
            MediaPlayer.Event.EncounteredError -> {
                Log.e("VLC", "Error: ${event}")
                // You can also show a user-friendly error message here
            }
            // ... handle other events
        }
    }

}