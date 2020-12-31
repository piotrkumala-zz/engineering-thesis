package com.example.myapplication.ui.home

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.myapplication.MainActivity
import com.example.myapplication.R
import com.example.myapplication.shared.ConnectionConfig
import com.google.android.material.textfield.TextInputEditText

class HomeFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val textView: TextView = root.findViewById(R.id.text_home)
        val serverText: TextInputEditText = root.findViewById(R.id.server_name)
        val user: TextInputEditText = root.findViewById(R.id.user)
        val password: TextInputEditText = root.findViewById(R.id.password)
        val devId: EditText = root.findViewById(R.id.dev_id)


        textView.text = getString(R.string.home_text)
        serverText.text = Editable.Factory.getInstance().newEditable("rainbow.fis.agh.edu.pl/meteo/connection.php")
        user.text = Editable.Factory.getInstance().newEditable("dustuser")
        password.text = Editable.Factory.getInstance().newEditable("user@dust")
        devId.text = Editable.Factory.getInstance().newEditable("13")

        val mainActivity: MainActivity = activity as MainActivity
        mainActivity.connectionConfig = ConnectionConfig(serverText.text.toString(), user.text.toString(), password.text.toString(), devId.text.toString().toInt())

        return root
    }
}