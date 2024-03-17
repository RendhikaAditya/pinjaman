package com.example.pinjamankredit.response

class User {
    data class Nasabah(
        val id: Int,
        val noKtp: String,
        val nama: String,
        val tanggalLahir: String,
        val jenisKelamin: String,
        val pekerjaan: String,
        val alamat: String,
        val telpon: String,
        val email: String
    )
}
