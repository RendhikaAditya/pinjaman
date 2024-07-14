package com.example.pinjamankredit.view.register

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.DatePicker
import android.widget.EditText
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


    private fun validateEmail(editText: EditText) {
        val email = editText.text.toString()
        if (!isValidEmail(email)) {
            editText.error = "Email tidak valid"
        } else {
            editText.error = null
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun validateNik(editText: EditText) {
        val nik = editText.text.toString()
        if (!isValidNik(nik)) {
            editText.error = "NIK harus 16 digit angka"
        } else {
            editText.error = null
        }
    }

    private fun isValidNik(nik: String): Boolean {
        return nik.length == 16 && nik.all { it.isDigit() }
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
        binding.noKtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateNik(binding.noKtp)
            }
        })

        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                validateEmail(binding.email)
            }

        })
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