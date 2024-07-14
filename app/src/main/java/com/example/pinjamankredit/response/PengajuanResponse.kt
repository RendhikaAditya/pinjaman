import java.io.Serializable

data class PengajuanResponse(
    val `data`: List<Data>,
    val pesan: String,
    val status: Int,
    val sukses: Boolean
): Serializable {
    data class Data(
        val alamat_kantor: String,
        val alamat_keluarga: String,
        val berkas_pinjaman: String,
        val dana_pinjaman_diajukan: String,
        val dana_pinjaman_diterima: String,
        val email_pasangan: String,
        val foto_bpkp: String,
        val foto_kk: String,
        val foto_ktp: String,
        val foto_stnk: String,
        val foto_unit: String,
        val hubungan_keluarga: String,
        val keterangan: String,
        val kode_nasabah: String,
        val kode_pp: String,
        val lama_ansuran: String,
        val nama_keluarga: String,
        val nama_pasangan: String,
        val nasabah: Nasabah,
        val nik_pasangan: String,
        val no_hp_keluarga: String,
        val no_hp_pasangan: String,
        val no_telpon_kantor: String,
        val pekerjaan: String,
        val penghasilan_bersih: String,
        val penghasilan_pasangan: String,
        val status_pengajuan: String,
        val tgl_pengajuan: String
    ): Serializable {
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


