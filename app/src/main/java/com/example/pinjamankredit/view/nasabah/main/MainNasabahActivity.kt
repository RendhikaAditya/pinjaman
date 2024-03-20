package com.example.pinjamankredit.view.nasabah.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityMainNasabahBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.login.LoginActivity
import com.example.pinjamankredit.view.nasabah.detailPengajuan.DetailPengjuanActivity
import com.example.pinjamankredit.view.nasabah.pengajuan.PengajuanNasabahActivity
import com.google.android.material.tabs.TabLayout
import java.text.NumberFormat
import java.util.Locale

class MainNasabahActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainNasabahBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: NasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: NasabahViewModelFactory
    lateinit var adapter: PengajuanNasabahAdapter
    lateinit var helper: Helper
    var pageActive = "konfirmasi"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainNasabahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = SharedPreferences(this)
        helper = Helper()
        setupListener()
        setupViewModel()
        setupObeserve()

    }

    private fun setupObeserve() {
        viewModel.fetchPengajuan(sharedPreferences.getString(Constants.KEY_id))
        viewModel.pengajuan.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    binding.progresBar.visibility = View.VISIBLE
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    binding.progresBar.visibility = View.GONE
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    var totalDana = 0
                    if (it.data.sukses) {
                        repeat(it.data.data.size){i->
                            if (it.data.data[i].status_pengajuan.equals("Di terima")){
                                totalDana = totalDana+it.data.data[i].dana_pinjaman_diterima.toInt()
                            }
                        }
                        binding.totalPinjaman.text = helper.formatRupiah(totalDana.toString())
                        adapter.setData(it.data.data)
                        binding.layoutKosong.visibility = View.GONE
                        binding.rvPengajuan.visibility = View.VISIBLE
                        adapter.filterDataByStatus(pageActive)
                        if (adapter.datas.isEmpty()){
                            binding.layoutKosong.visibility = View.VISIBLE
                        }
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size} :: $totalDana")
                    }else{
                        binding.layoutKosong.visibility = View.VISIBLE
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvPengajuan.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    binding.layoutKosong.visibility = View.VISIBLE
                    binding.progresBar.visibility = View.GONE
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = PengajuanNasabahAdapter(arrayListOf(),
            object : PengajuanNasabahAdapter.OnAdapterListener {
                override fun onClick(result: PengajuanResponse.Data) {
                    startActivity(Intent(this@MainNasabahActivity, DetailPengjuanActivity::class.java).putExtra("data", result))
                }

            }
        )
        binding.rvPengajuan.adapter = adapter
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = NasabahViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(NasabahViewModel::class.java)
    }

    private fun setupListener() {

        val navHeader = binding.navView.getHeaderView(0)
        val usernameTextView = navHeader.findViewById<TextView>(R.id.usernameTextView)
        val positionTextView = navHeader.findViewById<TextView>(R.id.positionTextView)
        usernameTextView.text = sharedPreferences.getString(Constants.KEY_NAMA)
        positionTextView.text = sharedPreferences.getString(Constants.KEY_LEVEL)

        binding.titleTextView1.text = sharedPreferences.getString(Constants.KEY_NAMA)
        if(!sharedPreferences.getBoolean(Constants.KEY_IS_LOGIN)){
            startActivity(Intent(this@MainNasabahActivity, LoginActivity::class.java))
            finish()
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
                R.id.nav_logout -> {
                    sharedPreferences.putBoolean(Constants.KEY_IS_LOGIN, false)
                    startActivity(
                        Intent(this@MainNasabahActivity, LoginActivity::class.java)
                    )
                    finish()
                    true
                }
                else -> false
            }
        }

        binding.btnBuatPengajuan.setOnClickListener {
            startActivity(Intent(this@MainNasabahActivity,PengajuanNasabahActivity::class.java))
        }


        with(binding){
            tabLayout.addTab(tabLayout.newTab().setText("Diajukan"))
            tabLayout.addTab(tabLayout.newTab().setText("Diterima"))
            tabLayout.addTab(tabLayout.newTab().setText("Ditolak"))
        }

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        pageActive = "konfirmasi"
                        viewModel.fetchPengajuan(sharedPreferences.getString(Constants.KEY_id))
                    }
                    1 ->{
                        pageActive = "Di Terima"
                        viewModel.fetchPengajuan(sharedPreferences.getString(Constants.KEY_id))
                    }
                    2 ->{
                        pageActive = "Di Tolak"
                        viewModel.fetchPengajuan(sharedPreferences.getString(Constants.KEY_id))
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
    }

}