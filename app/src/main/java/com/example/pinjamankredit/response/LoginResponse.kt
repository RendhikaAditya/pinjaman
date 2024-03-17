package com.example.pinjamankredit.response

data class LoginResponse(
    val sukses: Boolean,
    val status: Int,
    val pesan: String,
    val data: UserData?
){
    data class UserData(
        val id: Int,
        val nama: String,
        val level: String?
    )
}


