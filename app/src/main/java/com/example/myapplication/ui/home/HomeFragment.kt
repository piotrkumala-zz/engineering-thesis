package com.example.myapplication.ui.home

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TimePicker
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.ConnectionConfig
import com.example.myapplication.shared.HomeControls
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var controls: HomeControls
    private lateinit var mainActivity: MainActivity
    private val editable: Editable.Factory = Editable.Factory.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        mainActivity = activity as MainActivity
        initControls(root)
        return root
    }

    private fun initControls(root: View) {
        controls = HomeControls(
            root.findViewById(R.id.text_home),
            root.findViewById(R.id.server_name),
            root.findViewById(R.id.user),
            root.findViewById(R.id.password),
            root.findViewById(R.id.dev_id),
            root.findViewById(R.id.select_date),
        )
        controls.textView.text = getString(R.string.home_text)
        controls.selectDate.inputType = InputType.TYPE_NULL
        controls.selectDate.setOnClickListener {
            setupPickerDialogs(controls.selectDate, editable)
        }

        if (mainActivity.connectionConfig.value != ConnectionConfig()) {
            val config = mainActivity.connectionConfig.value
            controls.serverText.text = editable.newEditable(config?.ServerName)
            controls.user.text = editable.newEditable(config?.UserName)
            controls.password.text = editable.newEditable(config?.Password)
            controls.devId.text = editable.newEditable(config?.DevId.toString())
            controls.selectDate.text = editable.newEditable(config?.MeasurementDate)
        } else {
            controls.serverText.text =
                editable.newEditable("http://rainbow.fis.agh.edu.pl/meteo/connection.php")
            controls.user.text = editable.newEditable("dustuser")
            controls.password.text = editable.newEditable("user@dust")
            controls.devId.text = editable.newEditable("11")
            controls.selectDate.text = editable.newEditable("2020-12-20 10:00:00")
        }

        controls.textView.addTextChangedListener(afterTextChanged = changeListener())
        controls.serverText.addTextChangedListener(afterTextChanged = changeListener())
        controls.user.addTextChangedListener(afterTextChanged = changeListener())
        controls.password.addTextChangedListener(afterTextChanged = changeListener())
        controls.devId.addTextChangedListener(afterTextChanged = changeListener())
        controls.selectDate.addTextChangedListener(afterTextChanged = changeListener())

        mainActivity.connectionConfig.value = ConnectionConfig(
            controls.serverText.text.toString(),
            controls.user.text.toString(),
            controls.password.text.toString(),
            controls.devId.text.toString().toInt(),
            controls.selectDate.text.toString()
        )
    }


    private fun changeListener() = { _: Editable? ->

        mainActivity.connectionConfig.value = ConnectionConfig(
            controls.serverText.text.toString(),
            controls.user.text.toString(),
            controls.password.text.toString(),
            controls.devId.text.toString().toInt(),
            controls.selectDate.text.toString()
        )
    }

    private fun setupPickerDialogs(
        selectDate: EditText,
        editable: Editable.Factory
    ) {
        val calendar = Calendar.getInstance()
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)

        val picker = DatePickerDialog(requireContext(), { _, newYear, monthOfYear, dayOfMonth ->
            run {
                val utcFriendlyMonth =
                    if (monthOfYear < 10) "0${monthOfYear + 1}" else monthOfYear.toString()
                val utcFriendlyDay = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
                selectDate.text = editable.newEditable("$newYear-$utcFriendlyMonth-$utcFriendlyDay")
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _: TimePicker, hourOfDay: Int, minuteOfHour: Int ->
                        run {
                            val tmpText = selectDate.text
                            val utcFriendlyHour =
                                if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                            val utcFriendlyMinutes =
                                if (minuteOfHour < 10) "0$minuteOfHour" else minuteOfHour.toString()
                            selectDate.text =
                                editable.newEditable("$tmpText $utcFriendlyHour:$utcFriendlyMinutes:00")
                        }
                    },
                    0,
                    0,
                    true
                )
                timePicker.show()
            }

        }, year, month, day)
        picker.show()
    }
}