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
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.ConnectionConfig
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val editable = Editable.Factory.getInstance()

        val textView: TextView = root.findViewById(R.id.text_home)
        val serverText: TextInputEditText = root.findViewById(R.id.server_name)
        val user: TextInputEditText = root.findViewById(R.id.user)
        val password: TextInputEditText = root.findViewById(R.id.password)
        val devId: EditText = root.findViewById(R.id.dev_id)
        val selectDate: EditText = root.findViewById(R.id.select_date)
        selectDate.inputType = InputType.TYPE_NULL
        selectDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val month = calendar.get(Calendar.MONTH)
            val year = calendar.get(Calendar.YEAR)

            val picker = DatePickerDialog(requireContext(), { _, newYear, monthOfYear, dayOfMonth ->
                run {
                    selectDate.text = editable.newEditable("$newYear-$monthOfYear-$dayOfMonth")
                    val timePicker = TimePickerDialog(requireContext(), { _: TimePicker, hourOfDay: Int, minuteOfHour: Int ->
                        run {
                            val tmpText = selectDate.text
                            val utcFriendlyHour = if (hourOfDay < 10) "0$hourOfDay" else hourOfDay.toString()
                            val utcFriendlyMinutes = if (minuteOfHour < 10) "0$minuteOfHour" else minuteOfHour.toString()
                            selectDate.text = editable.newEditable("$tmpText $utcFriendlyHour:$utcFriendlyMinutes:00")
                        }
                    }, 0, 0, true)
                    timePicker.show()
                }

            }, year, month, day)
            picker.show()
        }


        textView.text = getString(R.string.home_text)
        serverText.text = editable.newEditable("rainbow.fis.agh.edu.pl/meteo/connection.php")
        user.text = editable.newEditable("dustuser")
        password.text = editable.newEditable("user@dust")
        devId.text = editable.newEditable("13")

        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.connectionConfig = ConnectionConfig(serverText.text.toString(), user.text.toString(), password.text.toString(), devId.text.toString().toInt())

        return root
    }
}