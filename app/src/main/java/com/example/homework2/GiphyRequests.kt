package com.example.homework2
import android.os.Parcel
import android.os.Parcelable
import androidx.versionedparcelable.ParcelField
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OrigImage(
    @SerialName("url") val url : String
)
@Serializable
data class Images(
    @SerialName("original") val original : OrigImage
)

@Serializable
data class GifData (
    @SerialName("title") val title: String,
    @SerialName("images") val images: Images
)

@Serializable
data class GiphyListRequest (
    val data: List<GifData>
)