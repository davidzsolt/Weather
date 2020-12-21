package com.example.weather.utils

class StringUtils {
    companion object {
        fun getIconURL(icon: String) : String =
            "https://www.metaweather.com/static/img/weather/png/$icon.png"
    }

}