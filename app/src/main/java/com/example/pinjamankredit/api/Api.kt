package com.example.pinjamankredit.api

import com.example.pinjamankredit.response.BaseResponse
import com.example.pinjamankredit.response.BayarResponse
import com.example.pinjamankredit.response.LoginResponse
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.response.PenggunaResponse
import com.example.pinjamankredit.response.PinjamanResponse
import com.example.pinjamankredit.response.SummaryResponse
import com.example.pinjamankredit.response.User
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.Base64


interface Api {

//    @GET("radius")
//    suspend fun radius(): Response<RadiusResponse>
//
//    @GET("rekap/{id}/{bulan}/{tahun}")
//    suspend fun rekapAbsen(
//        @Path("id") id: String,
//        @Path("bulan") bulan: String,
//        @Path("tahun") tahun : String
//    ): Response<RekapResponse>
//
    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("username_or_email") username: String,
        @Field("password") password: String,
    ): Response<LoginResponse>
//
    @GET("pengguna")
    suspend fun fetchPengguna(): Response<PenggunaResponse>

    @GET("bayar/{id}")
    suspend fun fetchBayar(@Path("id") id: String): Response<BayarResponse>

    @GET("bayar/status/{id}/{status}")
    suspend fun fetchUpdateStatusBayar(@Path("id") id: String,@Path("status") status: String): Response<BaseResponse>

    @FormUrlEncoded
    @POST("pengguna")
    suspend fun creadPengguna(
        @Field("nama_pengguna") nama: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("level") level:String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("pengguna/{id}")
    suspend fun updatePengguna(
        @Path("id") id : String,
        @Field("nama_pengguna") nama: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("level") level:String
    ): Response<BaseResponse>

    @DELETE("pengguna/{id}")
    suspend fun deletePengguna(
        @Path("id") id: Int
    ): Response<BaseResponse>


    @GET("nasabah")
    suspend fun fetchNasabah(): Response<NasabahResponse>

    @FormUrlEncoded
    @POST("nasabah")
    suspend fun creadNasabah(
        @Field("no_ktp") noKtp: String,
        @Field("nama_nasabah") nama: String,
        @Field("tgl_lahir") tglLahir: String,
        @Field("jenis_kelamin") jenisKelamin: String,
        @Field("pekerjaan") pekerjaan: String,
        @Field("alamat") alamat: String,
        @Field("telpon") telpon: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("status") status: String
    ): Response<BaseResponse>


    @FormUrlEncoded
    @POST("bayar/{jml}")
    suspend fun createBayar(
        @Path("jml") jml : String,
        @Field("kode_pp") kodepp: String,
        @Field("bulan_pembayaran") bulanBayar: String,
        @Field("nominal_bayaran") nominalBayar: String,
        @Field("status") status: String
    ): Response<BaseResponse>

    @FormUrlEncoded
    @POST("nasabah/{id}")
    suspend fun updateNasabah(
        @Path("id") id: String,
        @Field("no_ktp") noKtp: String,
        @Field("nama_nasabah") nama: String,
        @Field("tgl_lahir") tglLahir: String,
        @Field("jenis_kelamin") jenisKelamin: String,
        @Field("pekerjaan") pekerjaan: String,
        @Field("alamat") alamat: String,
        @Field("telpon") telpon: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("status") status: String
    ): Response<BaseResponse>

    @DELETE("nasabah/{id}")
    suspend fun deleteNasabah(
        @Path("id") id: String
    ): Response<BaseResponse>

    @GET("pengajuan-peminjaman")
    suspend fun fetchPengajuan(): Response<PengajuanResponse>

    @GET("summary")
    suspend fun fetchSummary(): Response<SummaryResponse>

    @GET("pengajuan-pinjaman/by-kode-nasabah/{id}")
    suspend fun fetchPengajuan(@Path("id") id:String): Response<PengajuanResponse>

    @GET("pinjaman")
    suspend fun fetchPinjaman(): Response<PinjamanResponse>

    @FormUrlEncoded
    @POST("pengajuan-nasabah/create")
    suspend fun createPengajuanPeminjaman(
        @Field("kode_nasabah") kodeNasabah: String,
        @Field("foto_ktp") fotoKtp : String,
        @Field("foto_kk") fotoKk : String,
        @Field("foto_unit") fotoUnit : String,
        @Field("dana_pinjaman_diajukan") danaPinjamanDiajukan: String,
        @Field("lama_ansuran") lamaAngsuran: String,
        @Field("berkas") berkas:String
    ):Response<BaseResponse>

    @FormUrlEncoded
    @POST("update-pengajuan/{id}")
    suspend fun updateStatusPinjaman(
        @Path("id") id: String,
        @Field("status_pengajuan") status: String,
        @Field("keterangan") keterangan: String,
        @Field("dana_pinjaman_diterima") dana: String,
    ):Response<BaseResponse>
}

