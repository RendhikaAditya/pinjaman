package com.example.pinjamankredit.view.admin.pinjaman

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPinjamanBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.response.PinjamanResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanAdapter
import com.example.pinjamankredit.view.admin.pengguna.PenggunaAdapter
import com.example.pinjamankredit.view.admin.pengguna.PenggunaViewModel
import com.example.pinjamankredit.view.admin.pengguna.PenggunaViewModelFactory

class PinjamanActivity : AppCompatActivity() {
    private lateinit var binding:ActivityPinjamanBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PinjamanViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PinjamanViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: PinjamanAdapter
    lateinit var helper : Helper
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinjamanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()

        setupListener()
        setupViewModel()
        setupObserve()
    }

    private fun setupObserve() {
        viewModel.fetchPinjaman()
        viewModel.pinjaman.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("Pinjaman", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("Pinjaman", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        binding.rvPinjaman.visibility = View.VISIBLE
                        Log.d("Pinjaman", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("Pinjaman", " :: Kosong")
                        binding.rvPinjaman.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Log.d("Pinjaman", " :: Error")
                }
            }
        })
        adapter = PinjamanAdapter(arrayListOf(),
            object : PinjamanAdapter.OnAdapterListener{
                override fun onClick(result: PinjamanResponse.Data) {
                    TODO("Not yet implemented")
                }

                override fun onClickShow(result: PinjamanResponse.Data) {
                    showPinjamanDataDialog(this@PinjamanActivity, result)
                }

                override fun onClickEdit(result: PinjamanResponse.Data) {
                }

                override fun onClickDelete(result: PinjamanResponse.Data) {

                }

            }
        )
        binding.rvPinjaman.adapter = adapter
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = PinjamanViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PinjamanViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
    }

    fun showPinjamanDataDialog(context: Context, data: PinjamanResponse.Data) {
        val dialogView = TextView(context).apply {
            // Menyiapkan teks untuk ditampilkan dalam dialog
            text = buildString {
                append("Nama         : ${data.nasabah.nama_nasabah}\n")
                append("Pinjaman     : ${helper.formatRupiah(data.dana_pinjaman)}\n")
                append("Ansuran      : ${data.lama_ansuran}\n")
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