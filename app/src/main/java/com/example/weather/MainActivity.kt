package com.example.weather

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.weather.data.SavedWeatherData
import com.example.weather.data.WeatherData
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.repository.RetrofitApiService
import com.example.weather.utils.SharedPrefUtils
import com.example.weather.utils.StringUtils
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level =
                    if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            })
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.metaweather.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val weatherService = retrofit.create(RetrofitApiService::class.java)


        var savedWeatherData: SavedWeatherData? = SharedPrefUtils.getTemp(this@MainActivity)
        if (savedWeatherData != null && savedWeatherData.savetime + 60000 > Calendar.getInstance().timeInMillis) {
            binding.temperatureName.text = savedWeatherData.tempName
            binding.temperature.text = savedWeatherData.temp
            loadImage(this@MainActivity, savedWeatherData.abbrev, binding.temperatureImage)
            Toast.makeText(
                this@MainActivity,
                getString(R.string.loading_from_cache),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            getWeatherData(weatherService, binding)
        }

        binding.swipeMainContainer.setOnRefreshListener {
            getWeatherData(weatherService, binding)
            binding.swipeMainContainer.isRefreshing = false
        }
    }

    private fun loadImage(context: Context, imagecode: String, imageView: ImageView) {
        Glide.with(context)
            .load(
                StringUtils.getIconURL(imagecode)

            ).into(imageView)
    }

    private fun getWeatherData(weatherService: RetrofitApiService, binding: ActivityMainBinding) {
        weatherService.getWeather().enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                val savedWeatherData: SavedWeatherData = SavedWeatherData(
                    response.body()?.consolidated_weather?.get(0)?.the_temp.toString(),
                    response.body()?.consolidated_weather?.get(0)?.weather_state_name.toString(),
                    response.body()?.consolidated_weather?.get(0)?.weather_state_abbr.toString(),
                    Calendar.getInstance().timeInMillis
                )

                binding.temperature.text = getString(
                    R.string.celsius_postfix,
                    savedWeatherData.temp
                )

                binding.temperatureName.text =
                    savedWeatherData.tempName

                loadImage(
                    this@MainActivity,
                    savedWeatherData.abbrev,
                    binding.temperatureImage
                )

                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.loaded_from_server),
                    Toast.LENGTH_SHORT
                ).show()

                SharedPrefUtils.saveTemp(
                    this@MainActivity, savedWeatherData
                )
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Toast.makeText(
                    this@MainActivity,
                    getString(R.string.refresh_error),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}