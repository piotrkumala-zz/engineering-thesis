package com.example.myapplication.shared

data class MapConfig(val SpinnerSelection: Int, val ColorBreakpoint: Int) {
    constructor() : this(0, 0)
}
