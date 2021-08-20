package com.chethan.demoproject

import android.util.Log
import com.chethan.demoproject.model.WeatherData
import retrofit2.Call
import retrofit2.Response

class DataRepository(val netWorkApi: NetWorkApi) {

    fun getWeatherData(lat: String,lon:String,appId:String,onWeatherData: OnWeatherData) {
        netWorkApi.getWeatherReport(lat,lon,appId).enqueue(object : retrofit2.Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                Log.e("success:","true")
                onWeatherData.onSuccess((response.body() as WeatherData))
            }
            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Log.e("success:",t.message)
                onWeatherData.onFailure()
            }
        })
    }

    interface OnWeatherData {
        fun onSuccess(data: WeatherData)
        fun onFailure()
    }


}

