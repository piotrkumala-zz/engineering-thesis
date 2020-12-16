package com.example.myapplication.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapplication.R
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class DashboardFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Part1
        val entries = ArrayList<Entry>()

//Part2
        entries.add(Entry(1f, 10f))
        entries.add(Entry(2f, 2f))
        entries.add(Entry(3f, 7f))

        ArrayList<Entry>()
        entries.add(Entry(3.5f, 0f))
        entries.add(Entry(4f, 20f))
        entries.add(Entry(5f, 16f))

        var newEntries = ArrayList<Entry>()
        GlobalScope.launch(Dispatchers.IO) {
            newEntries = prepareEntires(entries)
        }

//Part3

        val vl = LineDataSet(newEntries, "My Type")
//        val v2 = LineDataSet(entries1, "My Type")

        val legendEntries = ArrayList<LegendEntry>()
        val legendEntry = LegendEntry()
        legendEntry.label = "My Type"
        legendEntry.formColor = vl.color
        legendEntries.add(legendEntry)



//Part4
        vl.setDrawValues(true)
        vl.setDrawFilled(true)
        vl.lineWidth = 2f

        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val lineChart: LineChart = root.findViewById(R.id.lineChart)

        lineChart.xAxis.labelRotationAngle = 0f

//Part6
        lineChart.data = LineData(vl)
//        lineChart.data.addDataSet(v2)

        lineChart.legend.setCustom(legendEntries)

//Part7
        lineChart.axisRight.isEnabled = false
        lineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

//Part8
        lineChart.setTouchEnabled(true)
        lineChart.setPinchZoom(true)

        lineChart.data

//Part9
        lineChart.description.text = "Days"
        lineChart.setNoDataText("No forex yet!")

//Part10


//Part11
        return root

    }

    private fun prepareEntires(entries: ArrayList<Entry>): ArrayList<Entry> {
        return entries
    }

}