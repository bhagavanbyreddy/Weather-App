package com.chethan.demoproject


import com.chethan.demoproject.constants.WEATHER_API
import com.chethan.demoproject.model.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NetWorkApi{

    @GET(WEATHER_API)
    fun getWeatherReport(
        @Query("lat")
        lat: String,
        @Query("lon")
        lon: String,
        @Query("appid")
        appId: String
    ): Call<WeatherData>

}