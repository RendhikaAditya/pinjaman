package com.example.pinjamankredit.view.nasabah.pengajuan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.repositori.Repository

class PengajuanNasabahViewModelFactory(
    val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PengajuanNasabahViewModel(repository) as T
    }
}