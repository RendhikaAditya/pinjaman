package com.example.pinjamankredit.response

data class BayarResponse(
    val `data`: List<Data>,
    val pesan: String,
    val status: Int,
    val sukses: Boolean
){
    data class Data(
        val bulan_pembayaran: String,
        val id_bayar: String,
        val kode_pp: String,
        val nominal_bayaran: String,
        val status: String
    )
}
