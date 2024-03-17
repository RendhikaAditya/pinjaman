package com.example.pinjamankredit.view.admin.pengguna

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.repositori.Repository

class PenggunaViewModelFactory(
    val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PenggunaViewModel(repository) as T
    }
}