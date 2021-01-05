package com.github.pkumala.engineeringThesis.ui.mapSettings

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.DialogFragment
import com.github.engineeringThesis.R
import com.github.pkumala.engineeringThesis.MainActivity
import com.github.pkumala.engineeringThesis.shared.MapConfig
import com.google.android.material.textfield.TextInputEditText

class MapSettingsFragment : DialogFragment() {


    private lateinit var listener: NoticeDialogListener

    interface NoticeDialogListener {
        fun onDialogPositiveClick(dialog: DialogFragment)
        fun onDialogNegativeClick(dialog: DialogFragment)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = parentFragment as NoticeDialogListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                (context.toString() +
                        " must implement NoticeDialogListener")
            )
        }
    }

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
                    listener.onDialogPositiveClick(this)

                }
                .setNegativeButton(
                    R.string.cancel
                ) { _, _ ->
                    listener.onDialogPositiveClick(this)
                    dialog?.cancel()
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}