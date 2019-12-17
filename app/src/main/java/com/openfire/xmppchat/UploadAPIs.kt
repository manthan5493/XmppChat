package com.openfire.xmppchat

import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.Part
import retrofit2.http.Url


interface UploadAPIs {
    @Multipart
    fun uploadImage(@Url url: String, @Part file: MultipartBody.Part): Call<ResponseBody>
}