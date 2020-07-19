package com.example.photoviewer.viewmodel

import androidx.collection.ArrayMap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.photoviewer.model.Photo
import com.example.photoviewer.model.PhotoModel
import com.example.photoviewer.model.RetrofitConnect
import kotlinx.coroutines.*

class PhotoViewModel : ViewModel() {
    private val photoAPIServices = RetrofitConnect.getPhotoList()
    private lateinit var job: Job
    val photoList = MutableLiveData<ArrayList<Photo>>()
    private lateinit var photoModel: PhotoModel
    val isError = MutableLiveData<Boolean>()
    var page = 1
    var total = 0
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        CoroutineScope(Dispatchers.Main).launch {
            isError.value = false
        }

    }

    fun fetchPhotos() {
        val params = ArrayMap<String, Any>()
        params["method"] = "flickr.photos.getRecent"
        params["api_key"] = "0252b621d6df7e78640f870fb65703f5"
        params["format"] = "json"
        params["nojsoncallback"] = "1"
        params["page"] = page

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = photoAPIServices.getPhotoList("services/rest/?", params)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful) {
                    photoModel = response.body()!!
                    photoList.value = photoModel.photos.photo
                    page++
                    total = photoModel.photos.total
                } else {
                    isError.value = false
                }

            }
        }
    }
}