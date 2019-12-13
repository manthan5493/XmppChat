package com.openfire.xmppchat

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

public class NetworkClient {
    companion object {
        val BASE_URL = "";
        lateinit var retrofit: Retrofit
        public fun getRetrofitClient(context: Context): Retrofit {
            if (!(::retrofit.isInitialized)) {
                val okHttpClient = OkHttpClient.Builder()
                    .build();
                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            }
            return retrofit;
        }
    }
}