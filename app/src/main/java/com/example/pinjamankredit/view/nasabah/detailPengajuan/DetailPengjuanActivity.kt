package com.example.pinjamankredit.view.nasabah.detailPengajuan

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.pinjamankredit.R
import com.example.pinjamankredit.api.Api
import com.example.pinjamankredit.databinding.ActivityDetailPengjuanBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.ZoomableImageDialog

class DetailPengjuanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailPengjuanBinding
    private lateinit var helper: Helper
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailPengjuanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()

        setupListener()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupListener() {
        var data = intent.getSerializableExtra("data") as PengajuanResponse.Data
        with(binding){
            tglPengajuan.text = helper.ubahFormatTanggal(data.tgl_pengajuan)
            namaUser.text = data.nasabah.nama_nasabah
            danaPengajuan.text = helper.formatRupiah(data.dana_pinjaman_diajukan)
            pinjamanDiterima.text = helper.formatRupiah(data.dana_pinjaman_diterima)
            status.text = data.status_pengajuan
            keterangan.text = data.keterangan
            btnFotoKTP.setOnClickListener {
                val imageUrl = "${ApiService.imageURL}${data.foto_ktp}"
                helper.showImageDialog(this@DetailPengjuanActivity, imageUrl, "Foto KTP")
            }
            btnFotoKK.setOnClickListener {
                val imageUrl = "${ApiService.imageURL}${data.foto_kk}"
                helper.showImageDialog(this@DetailPengjuanActivity, imageUrl, "Foto KK")
            }
            btnFotoUnit.setOnClickListener {
                val imageUrl = "${ApiService.imageURL}${data.foto_unit}"
                helper.showImageDialog(this@DetailPengjuanActivity, imageUrl, "Foto Unit")
            }
            btnBack.setOnClickListener {
                finish()
            }
        }
    }
}