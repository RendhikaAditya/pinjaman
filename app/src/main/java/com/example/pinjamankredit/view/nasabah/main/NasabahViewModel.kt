package com.example.pinjamankredit.view.nasabah.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.PengajuanResponse
import kotlinx.coroutines.launch

class NasabahViewModel(
    val repository: Repository
) :ViewModel() {

    val pengajuan : MutableLiveData<Resource<PengajuanResponse>> = MutableLiveData()
//    val create : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
//    val delete : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
//    val update : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchPengajuan(id:String) = viewModelScope.launch {
        pengajuan.value = Resource.Loading()
        try {
            val response = repository.fetchPengajuan(id)
            pengajuan.value = Resource.Success(response.body()!!)
        } catch (e: Exception) {
            pengajuan.value = Resource.Error(e.message.toString())
        }
    }

//
//    fun createPengguna(nama:String, username:String, password:String, level:String) = viewModelScope.launch {
//        create.value = Resource.Loading()
//        try {
//            val response = repository.createPengguna(nama, username, password, level)
//            create.value = Resource.Success(response.body()!!)
//        }catch (e:java.lang.Exception){
//            create.value = Resource.Error(e.message.toString())
//        }
//    }
//
//    fun updatePengguna(id:String, nama:String, username:String, password:String, level:String) = viewModelScope.launch {
//        update.value = Resource.Loading()
//        try {
//            val response = repository.updatePengguna(id,nama, username, password, level)
//            update.value = Resource.Success(response.body()!!)
//        }catch (e:java.lang.Exception){
//            update.value = Resource.Error(e.message.toString())
//        }
//    }
//
//    fun deletePengguna(id:Int) = viewModelScope.launch {
//        delete.value = Resource.Loading()
//        try {
//            val response = repository.deletePengguna(id)
//            delete.value = Resource.Success(response.body()!!)
//        }catch (e:java.lang.Exception){
//            delete.value = Resource.Error(e.message.toString())
//        }
//    }

}