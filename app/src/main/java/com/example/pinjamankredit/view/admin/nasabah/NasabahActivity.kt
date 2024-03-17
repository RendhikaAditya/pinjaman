package com.example.pinjamankredit.view.admin.nasabah

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.databinding.ActivityNasabahBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.util.SharedPreferences

class NasabahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNasabahBinding
    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: NasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: NasabahViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: NasabahAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNasabahBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupViewModel()
        setupObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchNasabah()
    }

    private fun setupObserve() {
        viewModel.fetchNasabah()
        viewModel.nasabah.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        binding.rvNasabah.visibility = View.VISIBLE
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvNasabah.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = NasabahAdapter(arrayListOf(),
            object : NasabahAdapter.OnAdapterListener{
                override fun onClick(result: NasabahResponse.Data) {
                    TODO("Not yet implemented")
                }

                override fun onClickShow(result: NasabahResponse.Data) {
                    showNasabahDataDialog(this@NasabahActivity,result)
                }

                override fun onClickEdit(result: NasabahResponse.Data) {
                    startActivity(Intent(this@NasabahActivity, AddNasabahActivity::class.java).putExtra("nasabah",result).putExtra("edit", true))
                }

                override fun onClickDelete(result: NasabahResponse.Data) {
                    viewModel.deleteNasabah(result.kode_nasabah)

                }

            }
        )
        binding.rvNasabah.adapter = adapter

        viewModel.delete.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteNasabah", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteNasabah", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        viewModel.fetchNasabah()
                        Log.d("deleteNasabah", ":: Sukses")
                        Toast.makeText(this@NasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("deleteNasabah", " :: Kosong")
                        Toast.makeText(this@NasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteNasabah", " :: Error")
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

        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this@NasabahActivity, AddNasabahActivity::class.java).putExtra("edit", false))
        }
    }

    fun showNasabahDataDialog(context: Context, data: NasabahResponse.Data) {
        val dialogView = TextView(context).apply {
            // Menyiapkan teks untuk ditampilkan dalam dialog
            text = buildString {

                    append("Nama: ${data.nama_nasabah}\n")
                    append("Alamat: ${data.alamat}\n")
                    append("Email: ${data.email}\n")
                    append("Jenis Kelamin: ${data.jenis_kelamin}\n")
                    append("Kode Nasabah: ${data.kode_nasabah}\n")
                    append("No. KTP: ${data.no_ktp}\n")
                    append("Pekerjaan: ${data.pekerjaan}\n")
                    append("Status: ${data.status}\n")
                    append("Telepon: ${data.telpon}\n")
                    append("Tanggal Lahir: ${data.tgl_lahir}\n\n")
            }
            setPadding(50, 30, 50, 30)
        }

        // Membuat AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Data Nasabah")
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

}