package com.example.pinjamankredit.view.bayar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityBayarBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.BayarResponse
import PengajuanResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.headUnit.main.HeadViewModel
import com.example.pinjamankredit.view.headUnit.main.HeadViewModelFactory
import com.example.pinjamankredit.view.headUnit.main.PengajuanHeadAdapter
import com.example.pinjamankredit.view.nasabah.detailPengajuan.DetailPengjuanActivity

class BayarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBayarBinding
    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: BayarViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: BayarViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: BayarAdapter
    lateinit var helper: Helper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBayarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()
        sharedPreferences = SharedPreferences(this)

        binding.btnBack.setOnClickListener { finish() }

        setupViewModel()
        setupObserve()


    }



    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = BayarViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(BayarViewModel::class.java)
    }
    fun sumMoney(value1: String, jml: Int): String {
        val amount1 = value1.replace("Rp ", "").replace(",00", "").replace(".","").toInt()

        val sum = amount1 * jml

        Log.d("TAG", "sumMoney::: $sum")
        return "Rp " + String.format("%,d", sum).replace(",", ".") + ",00"
    }
    private fun setupObserve() {
        var kodePP = intent.getStringExtra("id")
        if (kodePP != null) {
            viewModel.fetchBayar(kodePP)
        }

        viewModel.bayar.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        var jmlBelum = 0
                        repeat(it.data.data.size){i->
                            if (!it.data.data[i].status.equals("Diterima")){
                                jmlBelum++
                            }
                        }
                        binding.totalAnsuran.text = "${sumMoney(it.data.data[0].nominal_bayaran, jmlBelum)}"
                        binding.totalAnsuran
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("deleteDeviceId", " :: Kosong")
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = BayarAdapter(arrayListOf(),
            object : BayarAdapter.OnAdapterListener{
                override fun onClick(result: BayarResponse.Data) {
                    TODO("Not yet implemented")
                }

                override fun onClickSetujui(model: BayarResponse.Data) {
                    viewModel.fetchUpdateStatusBayar(model.id_bayar, "Diterima")
                }


                override fun onClickBayar(model: BayarResponse.Data) {
                    viewModel.fetchUpdateStatusBayar(model.id_bayar, "Bayar")
                }

            }
        )
        binding.rvBayar.adapter = adapter


        viewModel.updateStatus.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("UpdateBayar", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("UpdateBayar", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        if (kodePP != null) {
                            viewModel.fetchBayar(kodePP)
                        }
                        Log.d("UpdateBayar", ":: Sukses = ${it.data}")
                    }else{
                        Log.d("UpdateBayar", " :: Kosong")
                    }
                }
                is Resource.Error -> {
                    Log.d("UpdateBayar", " :: Error")
                }
            }
        })

    }
}