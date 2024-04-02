package com.example.pinjamankredit.view.nasabah.pengajuan

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.PengajuanResponse
import kotlinx.coroutines.launch

class PengajuanNasabahViewModel(
    val repository: Repository
) :ViewModel() {

    val pengajuan : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
//    val create : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
//    val delete : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()
//    val update : MutableLiveData<Resource<BaseResponse>> = MutableLiveData()

    fun fetchPengajuan(
        kodeNasabah: String,
        fotoKtp : String,
        fotoKk : String,
        fotoUnit : String,
        danaPinjamanDiajukan: String,
        lamaAngsuran: String,
        berkas:String
    ) = viewModelScope.launch {
        pengajuan.value = Resource.Loading()
        try {
            val response = repository.fetchPengajuanNasabah(kodeNasabah, fotoKtp, fotoKk, fotoUnit, danaPinjamanDiajukan, lamaAngsuran, berkas)
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