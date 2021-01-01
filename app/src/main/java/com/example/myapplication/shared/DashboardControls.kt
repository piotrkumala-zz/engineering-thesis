package com.example.myapplication.shared

import com.github.mikephil.charting.charts.LineChart
import com.google.android.material.textfield.TextInputEditText

data class DashboardControls(val interval: TextInputEditText, val lineChart: LineChart)
