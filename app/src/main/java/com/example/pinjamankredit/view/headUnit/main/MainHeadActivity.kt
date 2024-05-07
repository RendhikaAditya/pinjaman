package com.example.pinjamankredit.view.headUnit.main

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityMainHeadBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.nasabah.NasabahActivity
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanActivity
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanAdapter
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanViewModel
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanViewModelFactory
import com.example.pinjamankredit.view.admin.pengguna.PenggunaActivity
import com.example.pinjamankredit.view.admin.pinjaman.PinjamanActivity
import com.example.pinjamankredit.view.login.LoginActivity
import com.example.pinjamankredit.view.nasabah.detailPengajuan.DetailPengjuanActivity
import com.example.pinjamankredit.view.nasabah.main.MainNasabahActivity
import com.google.android.material.tabs.TabLayout
import java.text.NumberFormat
import java.util.Locale

class MainHeadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainHeadBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: HeadViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: HeadViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: PengajuanHeadAdapter
    lateinit var helper: Helper
    var pageActive = "konfirmasi"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainHeadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()
        sharedPreferences = SharedPreferences(this)

        setupListener()
        setupViewModel()
        setupObserve()

    }

    fun hitungAngsuran(pinjaman: Int, durasi: Int): String {
        val totalAngsuranPerBulan = (pinjaman * 1.7) / durasi
        val totalAngsuranPerBulanBulat = Math.round(totalAngsuranPerBulan).toDouble()
//        return
        return "${helper.formatRupiah(totalAngsuranPerBulanBulat.toString())}"
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSummary()
    }

    private fun setupObserve() {
        viewModel.fetchPengajuan()
        viewModel.pengajuan.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progresBar.visibility = View.VISIBLE
                    binding.layoutKosong.visibility = View.GONE
                    binding.rvPengajuan.visibility = View.GONE
                    Log.d("deleteDeviceId", " :: Loading ")
                }

                is Resource.Success -> {
                    binding.progresBar.visibility = View.GONE
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        binding.layoutKosong.visibility = View.GONE
                        adapter.setData(it.data.data)
                        binding.rvPengajuan.visibility = View.VISIBLE
                        adapter.filterDataByStatus(pageActive)
                        if (adapter.datas.isEmpty()) {
                            binding.layoutKosong.visibility = View.VISIBLE
                        }
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    } else {
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvPengajuan.visibility = View.GONE
                        binding.layoutKosong.visibility = View.VISIBLE
                    }
                }

                is Resource.Error -> {
                    binding.progresBar.visibility = View.GONE
                    binding.layoutKosong.visibility = View.VISIBLE
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = PengajuanHeadAdapter(arrayListOf(),
            object : PengajuanHeadAdapter.OnAdapterListener {
                override fun onClick(result: PengajuanResponse.Data) {
                    startActivity(
                        Intent(
                            this@MainHeadActivity,
                            DetailPengjuanActivity::class.java
                        ).putExtra("data", result)
                    )
                }

                override fun onClickSetujui(model: PengajuanResponse.Data) {
                    val pinjamanArray = resources.getStringArray(R.array.pinjaman)
                    showInputDialog(
                        this@MainHeadActivity,
                        "Berapa dana pinjaman yang bisa diterima?",
                        pinjamanArray
                    ) { inputText ->
                        viewModel.fetchCreateBayar(
                            "${model.lama_ansuran.split(" ").get(0)}",
                            "${model.kode_pp}",
                            "1",
                            "${
                                hitungAngsuran(
                                    inputText.replace("Rp ",""). replace(",","").toInt(),
                                    model.lama_ansuran.split(" ").get(0).toInt()
                                )
                            }",
                            "Belum"
                        )
                        viewModel.fetchUpdate(
                            model.kode_pp,
                            "Di terima",
                            "Pinjaman Di setujui",
                            "${inputText.replace("Rp ",""). replace(",","")}"
                        )
                        Log.d("TAG", "onClickSetujui:: $inputText")
                    }
                }

                override fun onClickTolak(model: PengajuanResponse.Data) {
                    showRejectLoanDialog(this@MainHeadActivity, {
                        viewModel.fetchUpdate(model.kode_pp, "Di tolak", "Pijaman Ditolak", "0")
                    })
                }
            }
        )
        binding.rvPengajuan.adapter = adapter

        viewModel.update.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("UpdateStatus", " :: Loading ")
                }

                is Resource.Success -> {
                    Log.d("UpdateStatus", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        viewModel.fetchPengajuan()
                        Log.d("UpdateStatus", ":: Sukses")
                        Toast.makeText(
                            this@MainHeadActivity,
                            "${it.data.pesan}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("UpdateStatus", " :: Kosong")
                        Toast.makeText(
                            this@MainHeadActivity,
                            "${it.data.pesan}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Error -> {
                    Log.d("UpdateStatus", " :: Error")
                }
            }
        })

        viewModel.createBayar.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("bayar", " :: Loading ")
                }

                is Resource.Success -> {
                    Log.d("bayar", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        Log.d("bayar", ":: Sukses")
                        Toast.makeText(
                            this@MainHeadActivity,
                            "${it.data.pesan}",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Log.d("bayar", " :: Kosong")
                        Toast.makeText(
                            this@MainHeadActivity,
                            "${it.data.pesan}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Resource.Error -> {
                    Log.d("bayar", " :: Error")
                }
            }
        })


        viewModel.fetchSummary()
        viewModel.summary.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("Summary", " :: Loading ")
                }

                is Resource.Success -> {
                    Log.d("Summary", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        binding.txtTotalNasabahMeminjam.text = it.data.total_nasabah_pinjaman
                        binding.txtTotalNasabah.text = it.data.total_nasabah
                        binding.txtTotalPengguna.text = it.data.total_pengguna
                        binding.txtTotalPinjaman.text =
                            helper.formatRupiah(it.data.total_pinjaman_diterima)
                        Log.d("Summary", ":: Sukses")
                    } else {
                        Log.d("Summary", " :: Kosong")
                    }
                }

                is Resource.Error -> {
                    Log.d("Summary", " :: Error")
                }
            }
        })
    }

    fun showInputDialog(
        context: Context,
        title: String,
        itemList: Array<String>,
        callback: (String) -> Unit
    ) {
        val spinner = Spinner(context)
        val adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, itemList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        val dialog = AlertDialog.Builder(context)
            .setTitle(title)
            .setView(spinner)
            .setPositiveButton("OK") { _, _ ->
                val selectedItem = spinner.selectedItem.toString()
                callback.invoke(selectedItem)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }
            .create()

        dialog.show()
    }

    fun showRejectLoanDialog(context: Context, callback: () -> Unit) {
        val dialogTitle = "Konfirmasi Penolakan Pinjaman"
        val dialogMessage = "Apakah Anda yakin ingin menolak pinjaman ini?"

        val dialog = AlertDialog.Builder(context)
            .setTitle(dialogTitle)
            .setMessage(dialogMessage)
            .setPositiveButton("Ya") { _, _ ->
                // Panggil fungsi callback jika pengguna menyetujui penolakan pinjaman
                callback.invoke()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss() // Tutup dialog jika pengguna membatalkan
            }
            .create()

        dialog.show()
    }


    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = HeadViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HeadViewModel::class.java)
    }


    private fun setupListener() {
        val navHeader = binding.navView.getHeaderView(0)
        val usernameTextView = navHeader.findViewById<TextView>(R.id.usernameTextView)
        val positionTextView = navHeader.findViewById<TextView>(R.id.positionTextView)
        usernameTextView.text = sharedPreferences.getString(Constants.KEY_NAMA)
        positionTextView.text = sharedPreferences.getString(Constants.KEY_LEVEL)

        with(binding) {
            tabLayout.addTab(tabLayout.newTab().setText("Diajukan"))
            tabLayout.addTab(tabLayout.newTab().setText("Diterima"))
            tabLayout.addTab(tabLayout.newTab().setText("Ditolak"))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        pageActive = "konfirmasi"
                        viewModel.fetchPengajuan()
                    }

                    1 -> {
                        pageActive = "Di Terima"
                        viewModel.fetchPengajuan()
                    }

                    2 -> {
                        pageActive = "Di Tolak"
                        viewModel.fetchPengajuan()
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                // Kosongkan jika tidak ada tindakan khusus saat tab tidak dipilih
            }

            override fun onTabReselected(tab: TabLayout.Tab) {
                // Kosongkan jika tidak ada tindakan khusus saat tab dipilih kembali
            }
        })

        binding.titleTextView1.text = sharedPreferences.getString(Constants.KEY_NAMA)
        if (!sharedPreferences.getBoolean(Constants.KEY_IS_LOGIN)) {
            startActivity(Intent(this@MainHeadActivity, LoginActivity::class.java))
            finish()
        } else {
            if (sharedPreferences.getString(Constants.KEY_LEVEL).equals("nasabah")) {
                startActivity(Intent(this@MainHeadActivity, MainNasabahActivity::class.java))
                finish()
            }
        }

        // Set onClickListener pada tombol navIcon di toolbar
        binding.toolbar.setNavigationOnClickListener {
            if (binding.drawer.isDrawerOpen(binding.navView)) {
                binding.drawer.closeDrawer(binding.navView)
            } else {
                binding.drawer.openDrawer(binding.navView)
            }
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)


        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_nasabah -> {
                    startActivity(
                        Intent(this@MainHeadActivity, NasabahActivity::class.java)
                    )
                    true
                }

                R.id.nav_pinjaman -> {
                    startActivity(
                        Intent(this@MainHeadActivity, PinjamanActivity::class.java)
                    )
                    true
                }

                R.id.nav_logout -> {
                    startActivity(
                        Intent(this@MainHeadActivity, LoginActivity::class.java)
                    )
                    true
                }

                R.id.nav_pengguna -> {
                    sharedPreferences.putBoolean(Constants.KEY_IS_LOGIN, false)
                    startActivity(
                        Intent(this@MainHeadActivity, PenggunaActivity::class.java)
                    )
                    finish()
                    true
                }

                R.id.nav_laporan -> {
                    // URL yang akan dibuka
                    val url = "${ApiService.baseURL}laporan.php"

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))

                    startActivity(intent)
                    true
                }

                else -> false
            }
        }
    }
}