package com.example.pinjamankredit.repositori

import com.example.pinjamankredit.api.Api

class Repository(
    private val api: Api
) {
    suspend fun login(username: String, password: String) = api.login(username, password)

    suspend fun fetchPengguna() = api.fetchPengguna()
    suspend fun createPengguna(nama:String, username:String, password:String, level:String) = api.creadPengguna(nama, username, password, level)
    suspend fun updatePengguna(id: String, nama:String, username:String, password:String, level:String) = api.updatePengguna(id, nama, username, password, level)
    suspend fun deletePengguna(id:Int)= api.deletePengguna(id)
//
    suspend fun fetchNasabah() = api.fetchNasabah()
    suspend fun createNasabah(
        noKtp: String,
        nama: String,
        tglLahir: String,
        jenisKelamin: String,
        pekerjaan: String,
        alamat: String,
        telpon: String,
        email: String,
        password: String,
        status: String
    ) =api.creadNasabah(noKtp, nama, tglLahir, jenisKelamin, pekerjaan, alamat, telpon, email, password, status)
    suspend fun updateNasabah(
        id:String,
        noKtp: String,
        nama: String,
        tglLahir: String,
        jenisKelamin: String,
        pekerjaan: String,
        alamat: String,
        telpon: String,
        email: String,
        password: String,
        status: String
    ) =api.updateNasabah(id,noKtp, nama, tglLahir, jenisKelamin, pekerjaan, alamat, telpon, email, password, status)
    suspend fun deleteNasabah(id:String)= api.deleteNasabah(id)
//
    suspend fun fetchPengajuan(id: String) = api.fetchPengajuan(id)
    suspend fun fetchPengajuan() = api.fetchPengajuan()

    suspend fun fetchPinjaman() = api.fetchPinjaman()
    suspend fun fetchSummary() = api.fetchSummary()

    suspend fun fetchPengajuanNasabah(
        kodeNasabah: String,
        fotoKtp : String,
        fotoKk : String,
        fotoUnit : String,
        danaPinjamanDiajukan: String,
        lamaAngsuran: String
    ) = api.createPengajuanPeminjaman(kodeNasabah, fotoKtp, fotoKk, fotoUnit, danaPinjamanDiajukan, lamaAngsuran)

    suspend fun fetchUpdateStatus(
        id: String,
        status: String,
        keterngan:String,
        dana:String
    ) = api.updateStatusPinjaman(id,status,keterngan,dana)
}