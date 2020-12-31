package com.example.myapplication.shared

data class ConnectionConfig(val serverName: String, val userName: String, val Password: String, val DevId: Int) {
    constructor() : this("", "", "", 0)
}
