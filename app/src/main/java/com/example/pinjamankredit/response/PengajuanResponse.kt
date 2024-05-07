package com.example.pinjamankredit.response

import java.io.Serializable

data class PengajuanResponse(
    val `data`: List<Data>,
    val pesan: String,
    val status: Int,
    val sukses: Boolean
):Serializable{
    data class Data(
        val dana_pinjaman_diajukan: String,
        val dana_pinjaman_diterima: String,
        val foto_kk: String,
        val foto_ktp: String,
        val foto_unit: String,
        val foto_stnk: String,
        val foto_bpkp: String,
        val berkas_pinjaman: String,
        val keterangan: String,
        val kode_nasabah: String,
        val kode_pp: String,
        val lama_ansuran: String,
        val nasabah: Nasabah,
        val status_pengajuan: String,
        val tgl_pengajuan: String
    ):Serializable{
        data class Nasabah(
            val alamat: String,
            val email: String,
            val jenis_kelamin: String,
            val kode_nasabah: String,
            val nama_nasabah: String,
            val no_ktp: String,
            val password: String,
            val pekerjaan: String,
            val status: String,
            val telpon: String,
            val tgl_lahir: String
        ):Serializable
    }
}


