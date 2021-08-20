package com.chethan.demoproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.chethan.demoproject.DataRepository
import com.chethan.demoproject.model.WeatherData
import org.koin.standalone.KoinComponent


/**
 *Created by Bhagavan Byreddy on 20/08/21.
 */
class WeatherViewModel(val dataRepository: DataRepository) : ViewModel(), KoinComponent {

    var weatherData = MutableLiveData<WeatherData>()

    /*init {
        weatherData.value = WeatherData()
    }*/

    fun getWeatherData(lat: String, lon: String, appId: String) {
        dataRepository.getWeatherData(lat, lon, appId, object : DataRepository.OnWeatherData {
            override fun onSuccess(data: WeatherData) {
                weatherData.value = data
            }

            override fun onFailure() {
                //REQUEST FAILED
            }
        })
    }
}