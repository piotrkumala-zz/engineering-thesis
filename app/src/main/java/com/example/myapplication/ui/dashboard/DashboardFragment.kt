package com.example.myapplication.ui.dashboard

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.ChartConfig
import com.example.myapplication.shared.ConnectionConfig
import com.example.myapplication.shared.DashboardControls
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class DashboardFragment : Fragment() {

    private val model = DashboardViewModel()
    private lateinit var mainActivity: MainActivity
    private lateinit var controls: DashboardControls
    private val editable: Editable.Factory = Editable.Factory.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        val connectionConfig: ConnectionConfig = mainActivity.connectionConfig.value!!
        val chartConfig: ChartConfig = mainActivity.chartConfig.value!!
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        initControls(root, chartConfig)
        val spinner: Spinner = root.findViewById(R.id.spinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                requireContext(),
                R.array.measure_variable_array,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
        renderChart(connectionConfig, controls.lineChart)
        return root
    }

    private fun initControls(root: View, chartConfig: ChartConfig) {
        controls = DashboardControls(root.findViewById(R.id.interval), root.findViewById(R.id.lineChart))

        if (chartConfig != ChartConfig()) {
            controls.interval.text = editable.newEditable(chartConfig.TimeInterval.toString())
        }

        controls.interval.addTextChangedListener(afterTextChanged = changeListener())
    }

    private fun renderChart(connectionConfig: ConnectionConfig, lineChart: LineChart) {
        GlobalScope.launch(Dispatchers.IO) {
            val newEntries = prepareEntires(connectionConfig)

            val chartConfig = mainActivity.chartConfig.value!!
            val lineDataSets: Vector<LineDataSet> = getLineSets(newEntries, chartConfig)
//            val vl = LineDataSet(newEntries, "My Type")

            val legendEntries = ArrayList<LegendEntry>()
            val legendEntry = LegendEntry()
            legendEntry.label = "PM25"
            legendEntry.formColor = lineDataSets.firstElement().color
            legendEntries.add(legendEntry)


            for (item in lineDataSets) {
                item.setDrawValues(true)
//                item.setDrawFilled(true)
                item.lineWidth = 2f
            }

            lineChart.data = LineData(lineDataSets as List<ILineDataSet>?)
            lineChart.xAxis.labelRotationAngle = 90f
            lineChart.legend.setCustom(legendEntries)

            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            lineChart.setTouchEnabled(true)
            lineChart.setPinchZoom(true)

            lineChart.data

            lineChart.xAxis.valueFormatter = object : ValueFormatter() {
                @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
                @SuppressLint("SimpleDateFormat")
                override fun getFormattedValue(value: Float): String {
                    val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    val date = Date(value.toLong() + dateFormatter.parse(connectionConfig.MeasurementDate).time)
                    return dateFormatter.format(date)
                }
            }



            lineChart.description.isEnabled = false

            setupNightThemeForChart(lineChart)
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun getLineSets(newEntries: ArrayList<Entry>, chartConfig: ChartConfig): Vector<LineDataSet> {
        val sets = Vector<LineDataSet>(1)
        var lastCutIndex = 0
        newEntries.forEachIndexed { index, entry ->
            run {
                if (index + 1 < newEntries.size) {
                    if (abs(entry.x - newEntries[index + 1].x) > 1000 * chartConfig.TimeInterval) {
                        sets.add(LineDataSet(newEntries.subList(lastCutIndex, index + 1), "My Type"))
                        lastCutIndex = index + 1
                    }
                } else {
                    sets.add(LineDataSet(newEntries.subList(lastCutIndex, newEntries.size), "My Type"))
                }
            }
        }

        return sets
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

    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "UNCHECKED_CAST")
    @SuppressLint("SimpleDateFormat")
    private suspend fun prepareEntires(connectionConfig: ConnectionConfig): ArrayList<Entry> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val data = model.loadDataFromServer(connectionConfig)
        return data.map { item ->
            run {
                val date = dateFormatter.parse(item.DateTime)
                Entry(
                        (date.time - dateFormatter.parse(connectionConfig.MeasurementDate).time).toFloat(),
                        item.PM25.toFloat()
                )
            }

        } as ArrayList<Entry>
    }

    private fun changeListener() = { _: Editable? ->

        if (!controls.interval.text.isNullOrBlank())
            mainActivity.chartConfig.value = ChartConfig(
                    controls.interval.text.toString().toInt()
            )
        else
            mainActivity.chartConfig.value = ChartConfig(0)
    }

}