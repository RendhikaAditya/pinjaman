package com.example.pinjamankredit.view.admin.nasabah

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PenggunaResponse
import kotlinx.coroutines.launch

class NasabahViewModel(
    val repository: Repository
) :ViewModel() {

    val nasabah : MutableLiveData<Resource<NasabahResponse>> = MutableLiveData()
    val create : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val delete : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val update :MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchNasabah() = viewModelScope.launch {
        nasabah.value = Resource.Loading()
        try {
            val response = repository.fetchNasabah()
            nasabah.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            nasabah.value = Resource.Error(e.message.toString())
        }
    }


    fun createNasabah(
        noKtp: String,
        nama: String,
        tglLahir: String,
        jenisKelamin: String,
        pekerjaan: String,
        alamat: String,
        telpon: String,
        email: String,
        password: String,
        status: String
    ) = viewModelScope.launch {
        create.value = Resource.Loading()
        try {
            val response = repository.createNasabah(noKtp, nama, tglLahir, jenisKelamin, pekerjaan, alamat, telpon, email, password, status)
            create.value = Resource.Success(response.body()!!)
        }catch (e:java.lang.Exception){
            create.value = Resource.Error(e.message.toString())
        }
    }

    fun updateNasabah(
        id: String,
        noKtp: String,
        nama: String,
        tglLahir: String,
        jenisKelamin: String,
        pekerjaan: String,
        alamat: String,
        telpon: String,
        email: String,
        password: String,
        status: String
    ) = viewModelScope.launch {
        update.value = Resource.Loading()
        try {
            val response = repository.updateNasabah(id,noKtp, nama, tglLahir, jenisKelamin, pekerjaan, alamat, telpon, email, password, status)
            update.value = Resource.Success(response.body()!!)
        }catch (e:java.lang.Exception){
            update.value = Resource.Error(e.message.toString())
        }
    }

    fun deleteNasabah(id:String) = viewModelScope.launch {
        delete.value = Resource.Loading()
        try {
            val response = repository.deleteNasabah(id)
            delete.value = Resource.Success(response.body()!!)
        }catch (e:Exception){
            delete.value = Resource.Error(e.message.toString())
        }
    }

}