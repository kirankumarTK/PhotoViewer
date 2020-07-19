package com.example.photoviewer.model

import androidx.collection.ArrayMap
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Url

interface PhotoAPIServices {

    @FormUrlEncoded
    @POST
    suspend fun getPhotoList(
        @Url url: String,
        @FieldMap postParams: ArrayMap<String, Any>
    ): Response<PhotoModel>
}