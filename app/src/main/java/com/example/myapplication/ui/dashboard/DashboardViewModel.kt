package com.example.myapplication.ui.dashboard

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.api.MeasurementApi
import com.example.myapplication.shared.ConnectionConfig
import com.example.myapplication.shared.Measurement
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MultipartBody

class DashboardViewModel : ViewModel() {

    val data = MutableLiveData<List<Measurement>>()
    lateinit var adapter: ArrayAdapter<CharSequence>

    suspend fun loadDataFromServer(connectionConfig: ConnectionConfig): List<Measurement> {
        withContext(viewModelScope.coroutineContext) {
            try {
                val listResult = MeasurementApi.retrofitService.getProperties(
                    connectionConfig.ServerName,
                    Credentials.basic(connectionConfig.UserName, connectionConfig.Password),
                    MultipartBody.Builder().setType(MultipartBody.FORM)
                        .addFormDataPart("dev_id", connectionConfig.DevId.toString())
                        .addFormDataPart("datetime", connectionConfig.MeasurementDate.split(' ')[0])
                        .build(),
                )
                data.value = listResult
            } catch (e: Exception) {
                Log.e("Failure", e.message.toString())
            }
        }
        return data.value!!
    }
}