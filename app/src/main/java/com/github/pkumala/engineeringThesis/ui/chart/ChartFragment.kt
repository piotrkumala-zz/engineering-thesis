package com.github.pkumala.engineeringThesis.ui.chart

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.github.engineeringThesis.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils
import com.github.pkumala.engineeringThesis.MainActivity
import com.github.pkumala.engineeringThesis.shared.ChartConfig
import com.github.pkumala.engineeringThesis.shared.ConnectionConfig
import com.github.pkumala.engineeringThesis.shared.DashboardControls
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs


class ChartFragment : Fragment() {

    private val model = ChartViewModel()
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
        val root = inflater.inflate(R.layout.fragment_chart, container, false)
        initControls(root, chartConfig)
        renderChart(connectionConfig, chartConfig, controls.lineChart)
        return root
    }

    private fun initControls(root: View, chartConfig: ChartConfig) {
        controls = DashboardControls(
            root.findViewById(R.id.interval),
            root.findViewById(R.id.lineChart),
            root.findViewById(R.id.spinner)
        )
        model.adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.measure_variable_array,
            android.R.layout.simple_spinner_item
        )

        model.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        controls.spinner.adapter = model.adapter

        if (chartConfig != ChartConfig()) {
            controls.interval.text = editable.newEditable(chartConfig.TimeInterval.toString())
            controls.spinner.setSelection(chartConfig.SpinnerSelection)
        }

        controls.interval.addTextChangedListener(afterTextChanged = changeListener())

        controls.spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                mainActivity.chartConfig.value = ChartConfig(
                    if (controls.interval.text.toString()
                            .isNotBlank()
                    ) controls.interval.text.toString().toInt() else 0,
                    position
                )
                renderChart(
                    mainActivity.connectionConfig.value!!,
                    mainActivity.chartConfig.value!!,
                    controls.lineChart
                )
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }
    }

    private fun renderChart(
        connectionConfig: ConnectionConfig,
        chartConfig: ChartConfig,
        lineChart: LineChart
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            val newEntries = prepareEntires(connectionConfig, chartConfig)
            val lineDataSets: Vector<LineDataSet> = getLineSets(newEntries, chartConfig)
//            val vl = LineDataSet(newEntries, "My Type")

            val legendEntries = ArrayList<LegendEntry>()
            val legendEntry = LegendEntry()
            legendEntry.label = model.adapter.getItem(chartConfig.SpinnerSelection).toString()
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
                    val date =
                        Date(value.toLong() + dateFormatter.parse(connectionConfig.MeasurementDate).time)
                    return dateFormatter.format(date)
                }
            }

            lineChart.description.isEnabled = false

            setupNightThemeForChart(lineChart)
            lineChart.notifyDataSetChanged()
            lineChart.invalidate()
        }
    }

    private fun getLineSets(
        newEntries: ArrayList<Entry>,
        chartConfig: ChartConfig
    ): Vector<LineDataSet> {
        val sets = Vector<LineDataSet>(1)
        var lastCutIndex = 0
        newEntries.forEachIndexed { index, entry ->
            run {
                if (index + 1 < newEntries.size) {
                    if (abs(entry.x - newEntries[index + 1].x) > 1000 * chartConfig.TimeInterval) {
                        sets.add(
                            LineDataSet(
                                newEntries.subList(lastCutIndex, index + 1),
                                "My Type"
                            )
                        )
                        lastCutIndex = index + 1
                    }
                } else {
                    sets.add(
                        LineDataSet(
                            newEntries.subList(lastCutIndex, newEntries.size),
                            "My Type"
                        )
                    )
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
    private suspend fun prepareEntires(
        connectionConfig: ConnectionConfig,
        chartConfig: ChartConfig
    ): ArrayList<Entry> {
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        val data = model.loadDataFromServer(connectionConfig)
        return data.map { item ->
            run {
                val date = dateFormatter.parse(item.DateTime)
                Entry(
                    (date.time - dateFormatter.parse(connectionConfig.MeasurementDate).time).toFloat(),
                    when (chartConfig.SpinnerSelection) {
                        0 -> item.PM1.toFloat()
                        1 -> item.PM25.toFloat()
                        2 -> item.PM10.toFloat()
                        3 -> item.Temperature.toFloat()
                        4 -> item.RelativeHumidity.toFloat()
                        5 -> item.AtmosphericPressure.toFloat()
                        else -> item.PM1.toFloat()
                    }
                )
            }

        } as ArrayList<Entry>
    }

    private fun changeListener() = { _: Editable? ->

        if (!controls.interval.text.isNullOrBlank()) {
            mainActivity.chartConfig.value = ChartConfig(
                controls.interval.text.toString().toInt(),
                controls.spinner.selectedItemPosition
            )
            renderChart(
                mainActivity.connectionConfig.value!!,
                mainActivity.chartConfig.value!!,
                controls.lineChart
            )
        } else
            mainActivity.chartConfig.value = ChartConfig(0, 0)
    }

}