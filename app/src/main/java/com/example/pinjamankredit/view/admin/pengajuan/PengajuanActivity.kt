package com.example.pinjamankredit.view.admin.pengajuan

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPengajuanBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.nasabah.AddNasabahActivity
import com.example.pinjamankredit.view.admin.nasabah.NasabahAdapter
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModel
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModelFactory

class PengajuanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPengajuanBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PengajuanViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PengajuanViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: PengajuanAdapter
    lateinit var helper: Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengajuanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()

        setupListener()
        setupViewModel()
        setupObserve()
    }

    private fun setupObserve() {
        viewModel.fetchPengajuan()
        viewModel.pengajuan.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        binding.rvPengajuan.visibility = View.VISIBLE
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvPengajuan.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = PengajuanAdapter(arrayListOf(),
            object : PengajuanAdapter.OnAdapterListener{
                override fun onClick(result: PengajuanResponse.Data) {
                    TODO("Not yet implemented")
                }

                override fun onClickShow(result: PengajuanResponse.Data) {
                    showPengajuanDataDialog(this@PengajuanActivity, result)
                }

                override fun onClickEdit(result: PengajuanResponse.Data) {
                }

                override fun onClickDelete(result: PengajuanResponse.Data) {

                }

            }
        )
        binding.rvPengajuan.adapter = adapter
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = PengajuanViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PengajuanViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
    }
    fun showPengajuanDataDialog(context: Context, data: PengajuanResponse.Data) {
        val dialogView = TextView(context).apply {
            // Menyiapkan teks untuk ditampilkan dalam dialog
            text = buildString {
                append("Nama         : ${data.nasabah.nama_nasabah}\n")
                append("Pengajuan    : ${helper.formatRupiah(data.dana_pinjaman_diajukan)}\n")
                append("Disetujui    : ${helper.formatRupiah(data.dana_pinjaman_diterima)}\n")
                append("Ansuran      : ${data.lama_ansuran}\n")
                append("Status       : ${data.status_pengajuan}\n")
                append("Keterangan   : ${data.keterangan}\n")
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