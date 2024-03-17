package com.example.pinjamankredit.view.headUnit.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.repositori.Repository

class HeadViewModelFactory(
    val repository: Repository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HeadViewModel(repository) as T
    }
}