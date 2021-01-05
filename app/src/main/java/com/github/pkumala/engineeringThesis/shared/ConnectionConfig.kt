package com.github.pkumala.engineeringThesis.shared

data class ConnectionConfig(
    val ServerName: String,
    val UserName: String,
    val Password: String,
    val DevId: Int,
    val MeasurementDate: String,
) {
    constructor() : this("", "", "", 0, "")
}
