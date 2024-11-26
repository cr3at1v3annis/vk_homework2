package com.example.homework2

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration

import android.os.Bundle

import android.widget.ImageView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable



class DisplayOnTap : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gifDataUrl = intent.getStringExtra(Intent.EXTRA_LOCAL_ONLY) as? String
        setContent {
            Display(gifDataUrl)
        }
    }
    @Composable
    fun Display(gifDataUrl: String?) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (gifDataUrl != null) {
                OneGifItem(gifDataUrl)
            }
        }
    }
    @Composable
    fun OneGifItem(gifDataUrl: String, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AndroidView(factory = { context ->
                ImageView(context).apply {
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
            }, modifier = Modifier.fillMaxSize(),
                update = { imageView ->
                    Glide.with(imageView.context).load(gifDataUrl)
                        .diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        )
                        .into(imageView)
                }
            )
        }
    }
}