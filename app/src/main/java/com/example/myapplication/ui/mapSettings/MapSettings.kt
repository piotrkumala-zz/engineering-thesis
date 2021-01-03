package com.example.myapplication.ui.mapSettings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.MapConfig
import com.google.android.material.textfield.TextInputEditText

class MapSettings : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val root = inflater.inflate(R.layout.fragment_map_settings, null)
            val mainActivity = requireActivity() as MainActivity
            val editable: Editable.Factory = Editable.Factory.getInstance()

            val spinner: Spinner = root.findViewById(R.id.map_spinner)
            val colorBreakpoint: TextInputEditText = root.findViewById(R.id.color_breakpoint)
            val adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.measure_variable_array,
                android.R.layout.simple_spinner_item
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            val breakpoint = mainActivity.mapConfig.value?.ColorBreakpoint
            colorBreakpoint.text =
                editable.newEditable(if (breakpoint != -1) breakpoint.toString() else "0")
            val selection = mainActivity.mapConfig.value!!.SpinnerSelection
            spinner.setSelection(if (selection != 0) selection else 0)

            builder.setView(root)
                .setPositiveButton(
                    R.string.ok
                ) { _, _ ->
                    val selected = spinner.selectedItemPosition
                    val number = colorBreakpoint.text
                    mainActivity.mapConfig.value = MapConfig(
                        selected,
                        if (number.toString().isNotBlank()) number.toString().toInt() else 0
                    )

                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}