package com.example.pinjamankredit.view.admin.pengguna

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityAddPenggunaBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PenggunaResponse
import com.example.pinjamankredit.util.SharedPreferences

class AddPenggunaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddPenggunaBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PenggunaViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PenggunaViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddPenggunaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupViewModel()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.create.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("createPengguna", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("createPengguna", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("createPengguna", ":: Sukses")
                        Toast.makeText(this@AddPenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("createPengguna", " :: Kosong")
                        Toast.makeText(this@AddPenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("createPengguna", " :: Error")
                }
            }
        })
        viewModel.update.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("updatePengguna", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("updatePengguna", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("updatePengguna", ":: Sukses")
                        Toast.makeText(this@AddPenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("updatePengguna", " :: Kosong")
                        Toast.makeText(this@AddPenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("updatePengguna", " :: Error")
                }
            }
        })
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = PenggunaViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PenggunaViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }


        if (intent.getBooleanExtra("edit", false)){
            var data = intent.extras!!.get("pengguna") as PenggunaResponse.Data
            binding.nama.setText(data.nama_pengguna)
            binding.username.setText(data.username)
            // Tentukan posisi Spinner pekerjaan
            val pekerjaanArray = resources.getStringArray(R.array.level)
            val positionPekerjaan = pekerjaanArray.indexOf(data.level)
            if (positionPekerjaan != -1) {
                binding.spLevelUser.setSelection(positionPekerjaan)
            }

            binding.btnSimpan.setOnClickListener {
                viewModel.updatePengguna(
                    "${data.kode_pengguna}",
                    "${binding.nama.text}",
                    "${binding.username.text}",
                    "${binding.passwowrd.text}",
                    "${binding.spLevelUser.selectedItem}"
                )
            }
        }else{
            binding.btnSimpan.setOnClickListener {
                viewModel.createPengguna(
                    "${binding.nama.text}",
                    "${binding.username.text}",
                    "${binding.passwowrd.text}",
                    "${binding.spLevelUser.selectedItem}"
                )
            }
        }
    }
}