package com.example.pinjamankredit.view.admin.pengguna

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.PenggunaResponse
import kotlinx.coroutines.launch

class PenggunaViewModel(
    val repository: Repository
) :ViewModel() {

    val pengguna : MutableLiveData<Resource<PenggunaResponse>> = MutableLiveData()
    val create : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val delete : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
    val update : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchPengguna() = viewModelScope.launch {
        pengguna.value = Resource.Loading()
        try {
            val response = repository.fetchPengguna()
            pengguna.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            pengguna.value = Resource.Error(e.message.toString())
        }
    }


    fun createPengguna(nama:String, username:String, password:String, level:String) = viewModelScope.launch {
        create.value = Resource.Loading()
        try {
            val response = repository.createPengguna(nama, username, password, level)
            create.value = Resource.Success(response.body()!!)
        }catch (e:java.lang.Exception){
            create.value = Resource.Error(e.message.toString())
        }
    }

    fun updatePengguna(id:String, nama:String, username:String, password:String, level:String) = viewModelScope.launch {
        update.value = Resource.Loading()
        try {
            val response = repository.updatePengguna(id,nama, username, password, level)
            update.value = Resource.Success(response.body()!!)
        }catch (e:java.lang.Exception){
            update.value = Resource.Error(e.message.toString())
        }
    }

    fun deletePengguna(id:Int) = viewModelScope.launch {
        delete.value = Resource.Loading()
        try {
            val response = repository.deletePengguna(id)
            delete.value = Resource.Success(response.body()!!)
        }catch (e:java.lang.Exception){
            delete.value = Resource.Error(e.message.toString())
        }
    }

}