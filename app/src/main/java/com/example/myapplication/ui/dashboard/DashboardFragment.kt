package com.example.myapplication.ui.dashboard

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.init(context)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val lineChart: LineChart = root.findViewById(R.id.lineChart)

        //Part1
        val entries = ArrayList<Entry>()

//Part2
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))

        val entries1 = ArrayList<Entry>()
        entries1.add(Entry(3.5f, 0f))
        entries1.add(Entry(4f, 20f))
        entries1.add(Entry(5f, 16f))

        var newEntries: java.util.ArrayList<Entry>
        GlobalScope.launch(Dispatchers.IO) {
            newEntries = prepareEntires(entries)

            val vl = LineDataSet(newEntries, "My Type")
            val v2 = LineDataSet(entries1, "My Type")

            val legendEntries = ArrayList<LegendEntry>()
            val legendEntry = LegendEntry()
            legendEntry.label = "My Type"
            legendEntry.formColor = vl.color
            legendEntries.add(legendEntry)


            vl.setDrawValues(true)
            vl.setDrawFilled(true)
            vl.lineWidth = 2f

            v2.setDrawValues(true)
            v2.setDrawFilled(true)
            v2.lineWidth = 2f


            lineChart.xAxis.labelRotationAngle = 0f

            lineChart.data = LineData(vl)
            lineChart.data.addDataSet(v2)

            lineChart.legend.setCustom(legendEntries)

            lineChart.axisRight.isEnabled = false
            lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            lineChart.setTouchEnabled(true)
            lineChart.setPinchZoom(true)

            lineChart.data

            lineChart.description.isEnabled = false

//Part10
            if (context?.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
                lineChart.xAxis.axisLineColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.xAxis.gridColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.xAxis.textColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.axisLeft.axisLineColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.axisLeft.gridColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.axisLeft.textColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.legend.textColor = ContextCompat.getColor(requireContext(), R.color.white)
                lineChart.data.dataSets.forEach { set ->
                    set.valueTextColor = ContextCompat.getColor(requireContext(), R.color.white)
                }
            }
        }
        return root
    }

    private fun prepareEntires(entries: ArrayList<Entry>): ArrayList<Entry> {
        return entries
    }

}