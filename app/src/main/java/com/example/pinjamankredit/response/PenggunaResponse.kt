package com.example.pinjamankredit.response

import java.io.Serializable

data class PenggunaResponse(
    val `data`: List<Data>,
    val pesan: String,
    val status: Int,
    val sukses: Boolean
):Serializable{
    data class Data(
        val kode_pengguna: String,
        val level: String,
        val nama_pengguna: String,
        val password: String,
        val username: String
    ):Serializable
}
