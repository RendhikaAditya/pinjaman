package com.example.pinjamankredit.response

import java.io.Serializable

data class NasabahResponse(
    val `data`: List<Data>,
    val pesan: String,
    val status: Int,
    val sukses: Boolean
): Serializable {

    data class Data(
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
