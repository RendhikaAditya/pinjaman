package com.example.pinjamankredit.view.nasabah.detailPengajuan

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.example.pinjamankredit.R
import com.example.pinjamankredit.api.Api
import com.example.pinjamankredit.databinding.ActivityDetailPengjuanBinding
import com.example.pinjamankredit.network.ApiService
import PengajuanResponse
import android.view.View
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.PdfViewActivity
import com.example.pinjamankredit.util.ZoomableImageDialog
import com.example.pinjamankredit.view.bayar.BayarActivity

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
            binding.namaPasangan.text = data.nama_pasangan
            binding.nikPasangan.text = data.nik_pasangan
            binding.noHpPasangan.text = data.no_hp_pasangan
            binding.emailPasangan.text = data.email_pasangan

            binding.pekerjaan.text = data.pekerjaan
            binding.alamatKantor.text = data.alamat_kantor
            binding.noTelponKantor.text = data.no_telpon_kantor

            binding.namaKeluarga.text = data.nama_keluarga
            binding.hubunganKeluarga.text = data.hubungan_keluarga
            binding.alamatKeluarga.text = data.alamat_keluarga
            binding.noHpKeluarga.text = data.no_hp_keluarga

            binding.penghasilanBersih.text = helper.formatRupiah(data.penghasilan_bersih)
            binding.penghasilanPasangan.text = helper.formatRupiah(data.penghasilan_pasangan)
            if (data.status_pengajuan=="Di terima"){
                btnPembayaran.visibility = View.VISIBLE
            }else{
                btnPembayaran.visibility = View.GONE
            }
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

            btnFotoStnk.setOnClickListener {
                val imageUrl = "${ApiService.imageURL}${data.foto_stnk}"
                helper.showImageDialog(this@DetailPengjuanActivity, imageUrl, "Foto Stnk")
            }

            btnFotoBPKP.setOnClickListener {
                val imageUrl = "${ApiService.imageURL}${data.foto_bpkp}"
                helper.showImageDialog(this@DetailPengjuanActivity, imageUrl, "Foto Bpkp")
            }


            btnBack.setOnClickListener {
                finish()
            }

            btnPembayaran.setOnClickListener {
                startActivity(Intent(this@DetailPengjuanActivity, BayarActivity::class.java).putExtra("id", data.kode_pp))
            }
        }
    }
}