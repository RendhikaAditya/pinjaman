package com.example.pinjamankredit.response

data class SummaryResponse(
    val pesan: String,
    val status: Int,
    val sukses: Boolean,
    val total_nasabah: String,
    val total_nasabah_pinjaman: String,
    val total_pengguna: String,
    val total_pinjaman_diterima: String
)