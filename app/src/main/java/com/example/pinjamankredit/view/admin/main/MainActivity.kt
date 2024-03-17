package com.example.pinjamankredit.view.admin.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityMainBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.nasabah.NasabahActivity
import com.example.pinjamankredit.view.admin.pengajuan.PengajuanActivity
import com.example.pinjamankredit.view.admin.pengguna.PenggunaActivity
import com.example.pinjamankredit.view.admin.pinjaman.PinjamanActivity
import com.example.pinjamankredit.view.headUnit.main.HeadViewModel
import com.example.pinjamankredit.view.headUnit.main.HeadViewModelFactory
import com.example.pinjamankredit.view.headUnit.main.MainHeadActivity
import com.example.pinjamankredit.view.headUnit.main.PengajuanHeadAdapter
import com.example.pinjamankredit.view.login.LoginActivity
import com.example.pinjamankredit.view.nasabah.main.MainNasabahActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferences: SharedPreferences
    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: HeadViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: HeadViewModelFactory
    lateinit var helper: Helper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = SharedPreferences(this)
        helper = Helper()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)


        setupListener()
        setupViewModel()
        setupObserve()
    }

    override fun onResume() {
        super.onResume()
        viewModel.fetchSummary()
    }

    private fun setupObserve() {
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
                        binding.txtTotalPinjaman.text =helper.formatRupiah(it.data.total_pinjaman_diterima)
                        Log.d("Summary", ":: Sukses")
                    }else{
                        Log.d("Summary", " :: Kosong")
                    }
                }
                is Resource.Error -> {
                    Log.d("Summary", " :: Error")
                }
            }
        })
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = HeadViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(HeadViewModel::class.java)
    }

    private fun setupListener() {
        binding.titleTextView1.text = sharedPreferences.getString(Constants.KEY_NAMA)
        if(!sharedPreferences.getBoolean(Constants.KEY_IS_LOGIN)){
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }else{
            if (sharedPreferences.getString(Constants.KEY_LEVEL).equals("nasabah")){
                startActivity(Intent(this@MainActivity, MainNasabahActivity::class.java))
                finish()
            }
            if (sharedPreferences.getString(Constants.KEY_LEVEL).equals("nasabah")){
                startActivity(Intent(this@MainActivity, MainHeadActivity::class.java))
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
                        Intent(this@MainActivity, NasabahActivity::class.java)
                    )
                    true
                }
                R.id.nav_pinjaman -> {
                    startActivity(
                        Intent(this@MainActivity, PinjamanActivity::class.java)
                    )
                    true
                }
                R.id.nav_ganti_password -> {
//                    startActivity(Intent(this@MainActivity, GantiPasswordActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    sharedPreferences.putBoolean(Constants.KEY_IS_LOGIN, false)

                    startActivity(
                        Intent(this@MainActivity, LoginActivity::class.java)
                    )
                    finish()

                    true
                }
                R.id.nav_pengguna -> {
                    startActivity(
                        Intent(this@MainActivity, PenggunaActivity::class.java)
                    )
                    true
                }
                R.id.nav_pengajuan ->{
                    startActivity(
                        Intent(this@MainActivity, PengajuanActivity::class.java)
                    )
                    true
                }
                else -> false
            }
        }
    }
}