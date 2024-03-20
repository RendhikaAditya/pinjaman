package com.example.pinjamankredit.view.register

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityRegisterBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.main.MainActivity
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModel
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModelFactory
import java.util.Calendar
import java.util.Date
import java.util.Locale

class    RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var calendar: Calendar

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: NasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: NasabahViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var helper: Helper

    var tglSql : String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendar = Calendar.getInstance()

        setupListener()
        setupViewModel()
        setupObserver()
    }

    private fun setupObserver() {
        viewModel.create.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.btnRegister.text = ""
                    binding.progresBar.visibility = View.VISIBLE
                    Log.d("createNasabah", " :: Loading ")
                }
                is Resource.Success -> {
                    binding.btnRegister.text = "Register"
                    binding.progresBar.visibility = View.GONE
                    Log.d("createNasabah", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("createNasabah", ":: Sukses")
                        Toast.makeText(this@RegisterActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("createNasabah", " :: Kosong")
                        Toast.makeText(this@RegisterActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    binding.btnRegister.text = "Register"
                    binding.progresBar.visibility = View.GONE
                    Log.d("createNasabah", " :: Error")
                }
            }
        })
    }


    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = NasabahViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NasabahViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnMasuk.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener {
                viewModel.createNasabah(
                    "${binding.noKtp.text}",
                    "${binding.namaNasabah.text}",
                    "${tglSql}",
                    "${binding.spJeniskelamin.selectedItem}",
                    "${binding.spPekerjaan.selectedItem}",
                    "${binding.alamat.text}",
                    "${binding.noTelepon.text}",
                    "${binding.email.text}",
                    "${binding.passwowrd.text}",
                    "tidak aktif"
                )
        }

        binding.tglLahir.setOnClickListener { showDatePickerDialog() }

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Do something with the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Example: You can display the selected date
                binding.tglLahir.text = formatDate(selectedDate.time)
            },
            calendar.get(Calendar.YEAR), // Initial year selection
            calendar.get(Calendar.MONTH), // Initial month selection
            calendar.get(Calendar.DAY_OF_MONTH) // Initial day selection
        )

        datePicker.show()
    }

    private fun formatDate(date: Date): String {
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }
}