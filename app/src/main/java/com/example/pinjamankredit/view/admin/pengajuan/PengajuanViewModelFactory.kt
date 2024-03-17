package com.example.pinjamankredit.view.admin.pengajuan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.repositori.Repository

class PengajuanViewModelFactory(
    val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PengajuanViewModel(repository) as T
    }
}