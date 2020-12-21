package com.example.weather.utils

import android.content.Context
import com.example.weather.R
import com.example.weather.data.SavedWeatherData

class SharedPrefUtils {
    companion object {
        fun getTemp(context: Context): SavedWeatherData? {

            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedpref_file_key), Context.MODE_PRIVATE
            )

            return if (sharedPref.contains(context.getString(R.string.sharedpref_temp))
                && sharedPref.contains(context.getString(R.string.sharedpref_icon_name))
                && sharedPref.contains(context.getString(R.string.sharedpref_temp_name))
            ) {
                SavedWeatherData(
                    sharedPref.getString(context.getString(R.string.sharedpref_temp), "")!!,
                    sharedPref.getString(context.getString(R.string.sharedpref_temp_name), "")!!,
                    sharedPref.getString(context.getString(R.string.sharedpref_icon_name), "")!!,
                    sharedPref.getLong(context.getString(R.string.sharedpref_date), 0)
                )
            } else {
                null
            }
        }

        fun saveTemp(context: Context, savedWeatherData: SavedWeatherData) {
            val sharedPref = context.getSharedPreferences(
                context.getString(R.string.sharedpref_file_key), Context.MODE_PRIVATE
            )

            sharedPref.edit()
                .putString(context.getString(R.string.sharedpref_temp), savedWeatherData.temp)
                .putString(context.getString(R.string.sharedpref_temp_name), savedWeatherData.tempName)
                .putString(context.getString(R.string.sharedpref_icon_name), savedWeatherData.abbrev)
                .putLong(context.getString(R.string.sharedpref_date), savedWeatherData.savetime).apply()

        }

    }

}