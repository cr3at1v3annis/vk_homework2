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


class MainActivity : ComponentActivity() {
    private val giphyRepository by lazy { GiphyRepository(BuildConfig.GIPHY_URL) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrendingGifsScreen(giphyRepository)
        }
    }

    @Composable
    fun TrendingGifsScreen(giphyRepository: GiphyRepository) {
        var gifList by rememberSaveable { mutableStateOf<List<GifData>?>(null) }
        var isLoading by rememberSaveable { mutableStateOf(false) }
        var hasError by rememberSaveable { mutableStateOf(false) }
        val isLandscape =
            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
        val coroutineScope = rememberCoroutineScope()
        var state by rememberSaveable { mutableStateOf(false) }
        val handler = CoroutineExceptionHandler { _, exception ->
            run {
                isLoading = false
                hasError = true
                Toast.makeText(baseContext, exception.message ?: "", Toast.LENGTH_LONG).show()
            }
        }


        Column(modifier = Modifier.fillMaxSize()) {
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                hasError -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column {
                            Box(modifier = Modifier.clickable(onClick = {
                                showTrendingGifs(
                                    coroutineScope,
                                    giphyRepository,
                                    BuildConfig.API_KEY,
                                    handler,
                                    { gifList = it },
                                    { isLoading = it },
                                    { hasError = it },
                                    baseContext
                                )

                            })) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = getString(R.string.refresh),
                                    tint = Color.Red, modifier = Modifier.size(30.dp)
                                )
                            }
                        }
                    }
                }

                else -> {
                    if (isLandscape) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(10.dp)
                        )
                        {
                            items(gifList ?: emptyList()) { gifData ->
                                OneGifItem(gifData)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        )
                        {
                            items(gifList ?: emptyList()) { gifData ->
                                OneGifItem(gifData)
                            }
                        }
                    }
                }
            }
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    coroutineScope.launch(handler) {}
                    showTrendingGifs(
                        coroutineScope,
                        giphyRepository,
                        BuildConfig.API_KEY,
                        handler,
                        { gifList = it },
                        { isLoading = it },
                        { hasError = it },
                        baseContext
                    )
                }, modifier = Modifier.weight(1f)) {
                    Text(text = getString(R.string.updateGifsTrending))
                }
            }
        }
    }

    @SuppressLint("UnsafeIntentLaunch")
    @Composable
    fun OneGifItem(gifData: GifData, modifier: Modifier = Modifier) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_CENTER
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(onClick = {
                        //intent.putExtra("url", gifData.images.original.url)
                        intent = Intent(this@MainActivity, DisplayOnTap::class.java).apply {
                            putExtra("gifdata", gifData.images.original.url)
                        }
                        startActivity(intent)
                    }),
                update = { imageView ->
                    Glide.with(imageView.context).load(gifData.images.original.url)
                        .diskCacheStrategy(
                            DiskCacheStrategy.ALL
                        )
                        .into(imageView)
                },
            )
            Text(
                text = gifData.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center
            )
        }
    }


    private fun showTrendingGifs(
        coroutineScope: CoroutineScope,
        giphyRepository: GiphyRepository,
        apiKey: String,
        handler: CoroutineExceptionHandler,
        setGifList: (List<GifData>?) -> Unit,
        setLoading: (Boolean) -> Unit,
        setError: (Boolean) -> Unit,
        baseContext: Context
    ) {
        coroutineScope.launch(handler) {
            setLoading(true)
            setError(false)
            try {
                val response = giphyRepository.requestNTrendingGifs(apiKey, 15)
                setGifList(response?.data)
            } catch (e: Exception) {
                Toast.makeText(baseContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
                setError(true)
            } finally {
                setLoading(false)
            }
        }
    }

}