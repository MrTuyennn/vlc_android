package com.example.vlcjuncook.vlc

import android.content.ContentValues.TAG
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vlcjuncook.Adapter.ItemAdapter
import com.example.vlcjuncook.R
import com.example.vlcjuncook.databinding.ActivityVlcBinding

import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer

class VlcActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVlcBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityVlcBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dataset = arrayOf(
            "rtsp://admin:1234@goduck.dvrhost.net:554/video1",
            "rtsp://admin:1234@goduck.dvrhost.net:554/video2",
            "rtsp://admin:1234@goduck.dvrhost.net:554/video1",
        )
        val customAdapter = ItemAdapter(dataset)
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.adapter = customAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onPause() {
        super.onPause()
         Log.e("onPause","onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.e("onResume","onResume")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("onDestroy","onDestroy")
    }

}