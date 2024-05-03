package com.example.pinjamankredit.view.headUnit.main

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.LoginResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.response.SummaryResponse

import kotlinx.coroutines.launch

class HeadViewModel(
    val repository: Repository
) :ViewModel() {

    val update : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val pengajuan : MutableLiveData<Resource<PengajuanResponse>> = MutableLiveData()
    val summary : MutableLiveData<Resource<SummaryResponse>> = MutableLiveData()
    val createBayar : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchCreateBayar(jml: String,
                    kodePp: String,
                    blnBayar:String,
                    nominalBayar:String,
                         status:String
    ) = viewModelScope.launch {
        createBayar.value = Resource.Loading()
        try {
            val response = repository.createbayar(jml, kodePp, blnBayar, nominalBayar, status)
            createBayar.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            createBayar.value = Resource.Error(e.message.toString())
        }
    }

    fun fetchSummary() = viewModelScope.launch {
        summary.value = Resource.Loading()
        try {
            val response = repository.fetchSummary()
            summary.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            summary.value = Resource.Error(e.message.toString())
        }
    }

    fun fetchPengajuan() = viewModelScope.launch {
        pengajuan.value = Resource.Loading()
        try {
            val response = repository.fetchPengajuan()
            pengajuan.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            pengajuan.value = Resource.Error(e.message.toString())
        }
    }

    fun fetchUpdate(id: String,
                    status: String,
                    keterngan:String,
                    dana:String) = viewModelScope.launch {
        update.value = Resource.Loading()
        try {
            val response = repository.fetchUpdateStatus(id, status, keterngan, dana)
            update.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            update.value = Resource.Error(e.message.toString())
        }
    }
}