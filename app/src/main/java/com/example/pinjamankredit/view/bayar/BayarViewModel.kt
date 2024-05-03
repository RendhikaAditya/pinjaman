package com.example.pinjamankredit.view.bayar

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.BayarResponse
import com.example.pinjamankredit.response.LoginResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.response.SummaryResponse

import kotlinx.coroutines.launch

class BayarViewModel(
    val repository: Repository
) :ViewModel() {


    val bayar : MutableLiveData<Resource<BayarResponse>> = MutableLiveData()
    val updateStatus: MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchBayar(id:String) = viewModelScope.launch {
        bayar.value = Resource.Loading()
        try {
            val response = repository.fetchBayar(id)
            bayar.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            bayar.value = Resource.Error(e.message.toString())
        }
    }


    fun fetchUpdateStatusBayar(id:String, status:String) = viewModelScope.launch {
        updateStatus.value = Resource.Loading()
        try {
            val response = repository.fetchUpdateStatusBayar(id,status)
            updateStatus.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            updateStatus.value = Resource.Error(e.message.toString())
        }
    }


}