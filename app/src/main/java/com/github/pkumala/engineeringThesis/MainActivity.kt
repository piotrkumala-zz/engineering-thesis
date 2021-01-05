package com.github.pkumala.engineeringThesis

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.github.engineeringThesis.R
import com.github.pkumala.engineeringThesis.shared.ChartConfig
import com.github.pkumala.engineeringThesis.shared.ConnectionConfig
import com.github.pkumala.engineeringThesis.shared.MapConfig
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    val connectionConfig: MutableLiveData<ConnectionConfig> = MutableLiveData(ConnectionConfig())
    val chartConfig: MutableLiveData<ChartConfig> = MutableLiveData(ChartConfig())
    val mapConfig: MutableLiveData<MapConfig> = MutableLiveData(MapConfig())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}