package com.github.pkumala.engineeringThesis.ui.chart

import android.util.Log
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.pkumala.engineeringThesis.api.MeasurementApi
import com.github.pkumala.engineeringThesis.shared.ConnectionConfig
import com.github.pkumala.engineeringThesis.shared.Measurement
import kotlinx.coroutines.withContext
import okhttp3.Credentials
import okhttp3.MultipartBody

class ChartViewModel : ViewModel() {

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