package com.example.pinjamankredit.view.admin.nasabah

import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.widget.DatePicker
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityAddNasabahBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddNasabahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNasabahBinding
    private lateinit var calendar: Calendar

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: NasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: NasabahViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var helper: Helper

    var tglSql : String = ""
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNasabahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendar = Calendar.getInstance()
        helper = Helper()

        setupLister()
        setupViewModel()
        setupObserver()
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = NasabahViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NasabahViewModel::class.java)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupLister() {
        binding.btnBack.setOnClickListener { finish() }
        binding.tglLahir.setOnClickListener { showDatePickerDialog() }
        binding.noKtp.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateNik(binding.noKtp)
            }
        })

        binding.email.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                validateEmail(binding.email)
            }

        })


        if (intent.getBooleanExtra("edit",false)){
            var data = intent.extras!!.get("nasabah") as NasabahResponse.Data
            if (data != null) {
                binding.noKtp.setText(data.no_ktp.toString())
                binding.namaNasabah.setText(data.nama_nasabah)
                binding.tglLahir.text = if (!data.tgl_lahir.equals("0000-00-00")){helper.ubahFormatTanggal(data.tgl_lahir)}else{"0000-00-00"}

                // Tentukan posisi Spinner jenis kelamin
                val jenisKelaminArray = resources.getStringArray(R.array.jenis_kelamin)
                val positionJenisKelamin = jenisKelaminArray.indexOf(data.jenis_kelamin)
                if (positionJenisKelamin != -1) {
                    binding.spJeniskelamin.setSelection(positionJenisKelamin)
                }

                // Tentukan posisi Spinner pekerjaan
                val pekerjaanArray = resources.getStringArray(R.array.pekerjaan)
                val positionPekerjaan = pekerjaanArray.indexOf(data.pekerjaan)
                if (positionPekerjaan != -1) {
                    binding.spPekerjaan.setSelection(positionPekerjaan)
                }

                binding.alamat.setText(data.alamat)
                binding.noTelepon.setText(data.telpon)
                binding.email.setText(data.email)
            }

            binding.btnSimpan.setOnClickListener {
                viewModel.updateNasabah(
                    "${data.kode_nasabah}",
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
        }else{
            binding.btnSimpan.setOnClickListener {
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
        }

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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Do something with the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Example: You can display the selected date
                tglSql = formatDate(selectedDate.time)
                binding.tglLahir.text = helper.ubahFormatTanggal(formatDate(selectedDate.time))
            },
            calendar.get(Calendar.YEAR), // Initial year selection
            calendar.get(Calendar.MONTH), // Initial month selection
            calendar.get(Calendar.DAY_OF_MONTH) // Initial day selection
        )

        datePicker.show()
    }

    private fun formatDate(date: Date): String {
        val format = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return format.format(date)
    }

    private fun setupObserver() {
        viewModel.create.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("createNasabah", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("createNasabah", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("createNasabah", ":: Sukses")
                        Toast.makeText(this@AddNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("createNasabah", " :: Kosong")
                        Toast.makeText(this@AddNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("createNasabah", " :: Error")
                }
            }
        })

        viewModel.update.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("updateNasabah", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("updateNasabah", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("updateNasabah", ":: Sukses")
                        Toast.makeText(this@AddNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("updateNasabah", " :: Kosong")
                        Toast.makeText(this@AddNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("updateNasabah", " :: Error")
                }
            }
        })
    }
}