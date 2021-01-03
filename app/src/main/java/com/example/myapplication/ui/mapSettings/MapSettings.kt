package com.example.myapplication.ui.mapSettings

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.example.myapplication.R
import com.google.android.material.textfield.TextInputEditText

class MapSettings : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val root = inflater.inflate(R.layout.fragment_map_settings, null)
            val spinner: Spinner = root.findViewById(R.id.map_spinner)
            val colorBreakpoint: TextInputEditText = root.findViewById(R.id.color_breakpoint)
            val adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.measure_variable_array,
                android.R.layout.simple_spinner_item
            )

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

            builder.setView(root)
                .setPositiveButton(
                    R.string.ok
                ) { dialog, id ->
                    val selected = spinner.selectedItemPosition
                    val number = colorBreakpoint.text.toString().toInt()
                }
                .setNegativeButton(
                    R.string.cancel
                ) { dialog, id ->
                    getDialog()?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}