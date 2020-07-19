package com.example.photoviewer.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoModel(
    @SerializedName("photos")
    val photos: Photos,
    @SerializedName("message")
    val message: String,
    @SerializedName("code")
    val errorCode: Int
) : Parcelable

@Parcelize
data class Photos(
    @SerializedName("total")
    val total: Int,
    @SerializedName("photo")
    val photo: ArrayList<Photo>
) : Parcelable

@Parcelize
data class Photo(
    @SerializedName("id")
    val id: String,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("secret")
    val secret: String,
    @SerializedName("server")
    val server: String,
    @SerializedName("farm")
    val farm: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("ispublic")
    val isPublic: String,
    @SerializedName("isfriend")
    val isFriend: String,
    @SerializedName("isfamily")
    val isFamily: String
) : Parcelable