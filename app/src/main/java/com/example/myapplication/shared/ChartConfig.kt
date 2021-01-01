package com.example.myapplication.shared

data class ChartConfig(val TimeInterval: Int, val SpinnerSelection: Int) {
    constructor() : this(-1, 0)
}
