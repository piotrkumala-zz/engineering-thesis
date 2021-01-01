package com.example.myapplication.shared

import com.squareup.moshi.Json

data class Measurement(
    @Json(name = "id") val Id: Long,
    @Json(name = "dev_id") val DevId: Int,
    @Json(name = "datetime") val DateTime: String,
    @Json(name = "lat") val Latitude: Double,
    @Json(name = "lon") val Longitude: Double,
    @Json(name = "alt") val Altitude: Double,
    @Json(name = "speed") val Speed: Double,
    @Json(name = "pm1") val PM1: Double,
    @Json(name = "pm25") val PM25: Double,
    @Json(name = "pm10") val PM10: Double,
    @Json(name = "temp") val Temperature: Double,
    @Json(name = "r_hum") val RelativeHumidity: Double,
    @Json(name = "p_atm") val AtmosphericPressure: Double,
    @Json(name = "sat") val SatellitesCount: Int,
    @Json(name = "HDOP") val HorizontalPrecision: Double,
    @Json(name = "batt") val BatteryLevel: Double
)
