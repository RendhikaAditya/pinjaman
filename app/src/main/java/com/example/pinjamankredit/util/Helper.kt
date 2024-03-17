package com.example.pinjamankredit.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import com.bumptech.glide.request.transition.Transition
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.example.pinjamankredit.R
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.pow

class Helper {


    fun showImageDialog(context: Context, imageUrl: String, title: String) {
        // Inflating custom layout for dialog
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_image, null)

        // Finding views in custom layout
        val imageView = dialogView.findViewById<ImageView>(R.id.imageView)
        imageView.layoutParams.width = 600 // Ubah sesuai kebutuhan
        imageView.layoutParams.height = 800 // Ubah sesuai kebutuhan
        // Using Glide to load image from URL into ImageView
        Glide.with(context)
            .load(imageUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    // Setting loaded image into ImageView
                    imageView.setImageDrawable(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Not needed in this case
                }
            })

        // Creating AlertDialog.Builder instance
        val builder = AlertDialog.Builder(context)

        // Setting custom view and title to dialog
        builder.setView(dialogView)
            .setTitle(title)
        builder.setPositiveButton("Oke") { dialog, which ->

        }
        // Creating and showing AlertDialog
        val dialog = builder.create()
        dialog.show()
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        return "$year-$month-$day"
    }





    fun hitungJarak(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val radiusBumiKm = 6371000 // Radius Bumi dalam kilometer

        val lat1Rad = Math.toRadians(lat1)
        val lng1Rad = Math.toRadians(lng1)
        val lat2Rad = Math.toRadians(lat2)
        val lng2Rad = Math.toRadians(lng2)

        val deltaLat = lat2Rad - lat1Rad
        val deltaLng = lng2Rad - lng1Rad

        val a = Math.sin(deltaLat / 2).pow(2) + Math.cos(lat1Rad) * Math.cos(lat2Rad) * Math.sin(
            deltaLng / 2
        ).pow(2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        // Bulatkan nilai jarak ke 2 angka di belakang koma
        val jarakKm = Math.round(radiusBumiKm * c * 100) / 100.0
        return jarakKm
    }

    fun showTolakPeminjamanDialog(context: Context) {
        val builder = AlertDialog.Builder(context)

        // Mengatur judul dialog
        builder.setTitle("Tolak Peminjaman")

        // Mengatur teks dalam dialog
        builder.setMessage("Yakin Menolak Peminjaman kendaraan ini?")

        // Menambahkan tombol positif dengan teks "Yakin"
        builder.setPositiveButton("Yakin") { dialog, which ->

        }

        // Menambahkan tombol negatif dengan teks "Tidak"
        builder.setNegativeButton("Tidak") { dialog, which ->
            // Aksi yang akan dilakukan ketika tombol "Tidak" diklik
            // Anda dapat menambahkan logika lain jika diperlukan
            dialog.dismiss()
        }

        // Membuat dan menampilkan dialog
        val dialog = builder.create()
        dialog.show()
    }
    fun convertRupiahToNumber(rupiahString: String): String {
        val cleanString = rupiahString.replace(Regex("[^\\d]"), "")
        return if (cleanString.isNotEmpty()) cleanString.toString() else "0"
    }


    fun convertBulan(angkaBulan: Int): String {
        val bulan = arrayOf(
            "", "Januari", "Februari", "Maret", "April", "Mei", "Juni",
            "Juli", "Agustus", "September", "Oktober", "November", "Desember"
        )

        return bulan[angkaBulan]
    }

    fun generateHari(tanggal: Int, bulan: Int, tahun: Int): String {
        val calendar = Calendar.getInstance()
        calendar.set(tahun, bulan - 1, tanggal)

        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val hari = SimpleDateFormat("EEEE", Locale("id", "ID"))
        return hari.format(calendar.time)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun ubahFormatTanggal(tanggal: String): String {
        // Cek versi Android perangkat
        val version = Build.VERSION.SDK_INT

        // Jika versi Android >= Android O (26), gunakan metode DateTimeFormatter
        if (version >= Build.VERSION_CODES.O) {
            val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())
            val date = LocalDate.parse(tanggal, inputFormat)
            return date.format(outputFormat)
        } else {
            // Jika versi Android < Android O (26), gunakan metode SimpleDateFormat
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("EEEE, d MMMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(tanggal)
            return outputFormat.format(date)
        }
    }

    fun getHariIni(): String {
        val hari = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        return when (hari) {
            Calendar.SUNDAY -> "Minggu"
            Calendar.MONDAY -> "Senin"
            Calendar.TUESDAY -> "Selasa"
            Calendar.WEDNESDAY -> "Rabu"
            Calendar.THURSDAY -> "Kamis"
            Calendar.FRIDAY -> "Jumat"
            Calendar.SATURDAY -> "Sabtu"
            else -> "Hari tidak dikenal"
        }
    }

    fun getTanggal(): String {
        val tanggal = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val bulan = Calendar.getInstance().get(Calendar.MONTH) + 1
        val tahun = Calendar.getInstance().get(Calendar.YEAR)
        val bulanNama = arrayOf("Januari", "Februari", "Maret", "April", "Mei", "Juni", "Juli", "Agustus", "September", "Oktober", "November", "Desember")
        return "$tanggal ${bulanNama[bulan - 1]} $tahun"
    }

    fun getJam(): String {
        val jam = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val menit = Calendar.getInstance().get(Calendar.MINUTE)
        val detik = Calendar.getInstance().get(Calendar.SECOND)
        return "$jam:$menit:$detik"
    }

    fun formatRupiah(amountString: String): String {
        try {
            val amount = amountString.toDouble()
            val localeID = Locale("id", "ID")
            val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
            return formatRupiah.format(amount).replace("Rp", "Rp ")
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return "Invalid amount"
        }
    }
}