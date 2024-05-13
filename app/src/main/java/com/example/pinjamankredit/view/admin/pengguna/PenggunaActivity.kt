package com.example.pinjamankredit.view.admin.pengguna

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPenggunaBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PenggunaResponse
import com.example.pinjamankredit.util.PdfViewActivity
import com.example.pinjamankredit.util.SharedPreferences

class PenggunaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPenggunaBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PenggunaViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PenggunaViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: PenggunaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPenggunaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListener()
        setupViewModel()
        setupObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchPengguna()

    }

    private fun setupObserve() {
        viewModel.pengguna.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        binding.rvPengguna.visibility = View.VISIBLE
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvPengguna.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = PenggunaAdapter(arrayListOf(),
            object : PenggunaAdapter.OnAdapterListener{
                override fun onClick(result: PenggunaResponse.Data) {

                }

                override fun onClickShow(result: PenggunaResponse.Data) {
                    showPenggunaDataDialog(this@PenggunaActivity,result)
                }

                override fun onClickEdit(result: PenggunaResponse.Data) {
                    startActivity(Intent(this@PenggunaActivity, AddPenggunaActivity::class.java).putExtra("pengguna", result).putExtra("edit", true))
                }

                override fun onClickDelete(result: PenggunaResponse.Data) {
                    viewModel.deletePengguna(result.kode_pengguna.toInt())
                }
            }
        )
        binding.rvPengguna.adapter = adapter

        viewModel.delete.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deletePengguna", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deletePengguna", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        viewModel.fetchPengguna()
                        Log.d("deletePengguna", ":: Sukses")
                        Toast.makeText(this@PenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("deletePengguna", " :: Kosong")
                        Toast.makeText(this@PenggunaActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("deletePengguna", " :: Error")
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
        binding.btnBack.setOnClickListener { finish()}
        binding.btnTambah.setOnClickListener {
            startActivity(Intent(this@PenggunaActivity, AddPenggunaActivity::class.java).putExtra("edit", false))
        }
        binding.btnLaporan.setOnClickListener {
            val url = "${ApiService.baseURL}laporan_pengguna.php"

            val intent = Intent(this@PenggunaActivity, PdfViewActivity::class.java).putExtra("pdfUrl", url)

            startActivity(intent)
        }
    }

    fun showPenggunaDataDialog(context: Context, data: PenggunaResponse.Data) {
        val dialogView = TextView(context).apply {
            // Menyiapkan teks untuk ditampilkan dalam dialog
            text = buildString {
                append("Nama: ${data.nama_pengguna}\n")
                append("Username: ${data.username}\n")
                append("Level: ${data.level}\n")
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