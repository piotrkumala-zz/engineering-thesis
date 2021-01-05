package com.github.pkumala.engineeringThesis.shared

import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText

data class HomeControls(
    val textView: TextView,
    val serverText: TextInputEditText,
    val user: TextInputEditText,
    val password: TextInputEditText,
    val devId: TextInputEditText,
    val selectDate: TextInputEditText
)
