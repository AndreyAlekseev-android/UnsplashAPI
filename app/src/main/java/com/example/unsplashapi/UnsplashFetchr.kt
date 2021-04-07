package com.example.unsplashapi

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import api.PhotoResponse
import api.UnsplashApi
import api.UnsplashResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "UnsplashFetchr"

class UnsplashFetchr {
    private val unsplashApi: UnsplashApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        unsplashApi = retrofit.create(UnsplashApi::class.java)
    }

    fun fetchPhotos(): LiveData<List<GalleryItem>> {
        val responseLiveData: MutableLiveData<List<GalleryItem>> = MutableLiveData()
        val unsplashRequest: Call<UnsplashResponse> = unsplashApi.fetchPhotos()
        unsplashRequest.enqueue(object : Callback<UnsplashResponse> {

            override fun onFailure(call: Call<UnsplashResponse>, t: Throwable) {
                Log.e(TAG, "Failed to fetch photos", t)
            }

            override fun onResponse(
                call: Call<UnsplashResponse>,
                response: Response<UnsplashResponse>
            ) {
                Log.d(TAG, "Response received")
                val unsplashResponse: UnsplashResponse? = response.body()
                val photoResponse: PhotoResponse? = unsplashResponse?.photos
                var galleryItems: List<GalleryItem> = photoResponse?.galleryItems
                    ?: mutableListOf()
                galleryItems = galleryItems.filterNot {
                    it.url.isNullOrBlank()
                }
                responseLiveData.value = galleryItems
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = unsplashApi.fetchUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        Log.i(TAG, "Decoded bitmap=$bitmap from Response=$response")
        return bitmap
    }
}