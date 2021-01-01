package com.example.myapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.ConnectionConfig
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class DashboardFragment : Fragment() {

    private val model = DashboardViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val connectionConfig: ConnectionConfig = (activity as MainActivity).connectionConfig.value!!
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val lineChart: LineChart = root.findViewById(R.id.lineChart)

        renderChart(connectionConfig, lineChart)
        return root
    }

    private fun renderChart(connectionConfig: ConnectionConfig, lineChart: LineChart) {
        GlobalScope.launch(Dispatchers.IO) {
            val newEntries = prepareEntires(connectionConfig)

            val vl = LineDataSet(newEntries, "My Type")

            val legendEntries = ArrayList<LegendEntry>()
            val legendEntry = LegendEntry()
            legendEntry.label = "PM25"
            legendEntry.formColor = vl.color
            legendEntries.add(legendEntry)


            vl.setDrawValues(true)
            //            vl.setDrawFilled(true)
            vl.lineWidth = 2f



            lineChart.xAxis.labelRotationAngle = 90f
            lineChart.data = LineData(vl)
            lineChart.legend.setCustom(legendEntries)

            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            lineChart.setTouchEnabled(true)
            lineChart.setPinchZoom(true)

            lineChart.data

            lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                @SuppressLint("SimpleDateFormat")
                override fun getFormattedValue(value: Float): String {
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val date = Date(value.toLong())
                    return dateFormatter.format(date)
                }
            }



            lineChart.description.isEnabled = false

            setupNightThemeForChart(lineChart)
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun setupNightThemeForChart(lineChart: LineChart) {
        if (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            lineChart.xAxis.axisLineColor =
                    ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.xAxis.gridColor = ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.axisLeft.axisLineColor =
                    ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.axisLeft.gridColor =
                    ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.axisLeft.textColor =
                    ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.white)
            lineChart.data.dataSets.forEach { set ->
                set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
            }
        }
    }

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @SuppressLint("SimpleDateFormat")
    private suspend fun prepareEntires(connectionConfig: ConnectionConfig): ArrayList<Entry> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val data = model.loadDataFromServer(connectionConfig)
        return data.map { item ->
            Entry(
                    dateFormatter.parse(item.DateTime).time.toFloat(),
                    item.PM25.toFloat()
            )
        } as ArrayList<Entry>
    }

}