package com.chethan.demoproject.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.chethan.demoproject.R
import com.chethan.demoproject.constants.APP_ID
import com.chethan.demoproject.model.WeatherData
import com.chethan.demoproject.viewmodel.WeatherViewModel
import kotlinx.android.synthetic.main.weather_layout.*
import org.koin.android.viewmodel.ext.android.viewModel


/**
 *Created by Bhagavan Byreddy on 20/08/21.
 */
class WeatherFragment : Fragment() {


    var lat: String? = null
    var long: String? = null
    private val weatherViewModel: WeatherViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.weather_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            if (requireArguments().containsKey("lat")) {
                lat = requireArguments().getString("lat")
            }
            if (requireArguments().containsKey("long")) {
                long = requireArguments().getString("long")
            }
        }

        if(! long.isNullOrEmpty() && ! lat.isNullOrEmpty()) {
            getWeatherData(lat!!,long!!)
        }
    }

    private fun getWeatherData(lat: String, long: String) {
        weatherViewModel.getWeatherData(lat, long, APP_ID)
        weatherViewModel.weatherData.observe(
            viewLifecycleOwner,
            Observer(function = fun(weatherData: WeatherData?) {
                weatherData?.let {
                    temparatureValTv.text = weatherData.main?.temp?.toString()
                    pressureValTv.text = weatherData.main?.pressure?.toString()
                    himudityValTv.text = weatherData.main?.humidity?.toString()
                    windSpeedValTv.text = weatherData.wind?.speed?.toString()

                    mainCv.visibility = View.VISIBLE
                }
                progressBar.visibility = View.GONE

            })
        )
    }

}