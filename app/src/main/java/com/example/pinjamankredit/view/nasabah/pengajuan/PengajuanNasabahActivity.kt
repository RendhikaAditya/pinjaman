package com.example.pinjamankredit.view.nasabah.pengajuan

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.databinding.ActivityPengajuanNasabahBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.NumberFormat
import java.util.Locale

class PengajuanNasabahActivity : AppCompatActivity() {
    companion object {
        const val REQUEST_IMAGE_CAMERA_KK = 101
        const val REQUEST_IMAGE_GALLERY_KK = 102

        const val REQUEST_IMAGE_CAMERA_KTP = 103
        const val REQUEST_IMAGE_GALLERY_KTP = 104

        const val REQUEST_IMAGE_CAMERA_UNIT = 105
        const val REQUEST_IMAGE_GALLERY_UNIT = 106


        private const val REQUEST_PDF_FILE = 301
    }
    private var selectedImageKTP: Bitmap? = null
    private var selectedImageKK: Bitmap? = null
    private var selectedImageUnit: Bitmap? = null
    private var selectedPdfUri: Uri? = null


    private lateinit var binding: ActivityPengajuanNasabahBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PengajuanNasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PengajuanNasabahViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var helper: Helper


    private val PERMISSION_REQUEST_CODE = 1001
    private var downloadId: Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengajuanNasabahBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()
        sharedPreferences = SharedPreferences(this)


        setupListener()
        setupViewModel()
        setupObserve()

    }

    fun hitungAngsuran(pinjaman: Int, durasi: Int): Double {
        // Bunga per bulan (misalnya 1% per bulan)
        val bungaPerBulan = 0.0955
        // Menghitung total angsuran per bulan
        val totalAngsuranPerBulan = (pinjaman.toDouble() * bungaPerBulan) / (1 - (1 / Math.pow(1 + bungaPerBulan, durasi.toDouble())))
        return totalAngsuranPerBulan
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with download
                startDownload()
            } else {
                // Permission denied
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startDownload() {
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse("https://pii.or.id/uploads/dummies.pdf")
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "dummies.pdf"
        )
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        downloadId = downloadManager.enqueue(request)

        // Show progress bar
        binding.downloadText.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE

        // Register broadcast receiver to listen download complete event
        registerReceiver(downloadCompleteReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }

    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadId) {
                // Hide progress bar
                with(binding) {
                    progressBar.visibility = View.GONE
                    downloadText.visibility = View.GONE
                    openButton.visibility = View.VISIBLE
                }
            }
        }
    }

    fun openFile() {
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "dummies.pdf")
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Tambahkan ini untuk memberikan izin baca ke aplikasi lain
        startActivity(intent)
    }


    private fun setupObserve() {
        viewModel.pengajuan.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("pengajuanNasabah", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("pengajuanNasabah", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        finish()
                        Log.d("pengajuanNasabah", ":: Sukses")
                        Toast.makeText(this@PengajuanNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("pengajuanNasabah", " :: Kosong")
                        Toast.makeText(this@PengajuanNasabahActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Log.d("pengajuanNasabah", " :: Error")
                }
            }
        })
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = PengajuanNasabahViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PengajuanNasabahViewModel::class.java)
    }

    fun Double.toRupiahFormat(): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("id", "ID"))
        return formatter.format(this)
    }

    private fun setupListener() {
        with(binding){

            var ansuran = spAnsuran.selectedItem
            var pinjaman = spPinjaman.selectedItem

            val ansuranNum = ansuran.toString().replace("[^\\d]".toRegex(), "").toInt()
            val pinjamanNum = pinjaman.toString().replace("[^\\d]".toRegex(), "").toInt()



            binding.downloadLayout.setOnClickListener {
                startDownload()

            }
            binding.openButton.setOnClickListener { openFile() }

            danaPengajuan.addTextChangedListener(RupiahTextWatcher(danaPengajuan))

            imageViewKK.setOnClickListener {
                val options = arrayOf("Ambil dari Galeri", "Buka Kamera")
                val builder = AlertDialog.Builder(this@PengajuanNasabahActivity)
                builder.setTitle("Pilih Sumber Gambar")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Pilih dari Galeri
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_KK)
                        }
                        1 -> {
                            // Buka Kamera
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA_KK)
                        }
                    }
                }
                builder.show()
            }

            imageViewKTP.setOnClickListener {
                val options = arrayOf("Ambil dari Galeri", "Buka Kamera")
                val builder = AlertDialog.Builder(this@PengajuanNasabahActivity)
                builder.setTitle("Pilih Sumber Gambar")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Pilih dari Galeri
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_KTP)
                        }
                        1 -> {
                            // Buka Kamera
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA_KTP)
                        }
                    }
                }
                builder.show()
            }


            imageViewUnit.setOnClickListener {
                val options = arrayOf("Ambil dari Galeri", "Buka Kamera")
                val builder = AlertDialog.Builder(this@PengajuanNasabahActivity)
                builder.setTitle("Pilih Sumber Gambar")
                builder.setItems(options) { dialog, which ->
                    when (which) {
                        0 -> {
                            // Pilih dari Galeri
                            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            startActivityForResult(galleryIntent, REQUEST_IMAGE_GALLERY_UNIT)
                        }
                        1 -> {
                            // Buka Kamera
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAMERA_UNIT)
                        }
                    }
                }
                builder.show()
            }

            btnSelectFile.setOnClickListener {
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.type = "application/pdf"
                startActivityForResult(intent, REQUEST_PDF_FILE)
            }

            btnSimpan.setOnClickListener {
                viewModel.fetchPengajuan(
                    "${sharedPreferences.getString(Constants.KEY_id)}",
                    "${getStringImage(selectedImageKTP)}",
                    "${getStringImage(selectedImageKK)}",
                    "${getStringImage(selectedImageUnit)}",
                    "${pinjamanNum}",
                    "${binding.spAnsuran.selectedItem}",
                    "${getFileBase64(selectedPdfUri!!)}"
                )
                Log.w("TAG",

                    "HH ${binding.spAnsuran.selectedItem} PP ${binding.spPinjaman.selectedItem}\n ${hitungAngsuran(ansuranNum, pinjamanNum)}"

                )
            }
        }
    }

    @SuppressLint("Range")
    private fun getPDFFileName(uri: Uri): String? {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    result = it.getString(it.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != -1) {
                result = result?.substring(cut!! + 1)
            }
        }
        return result
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PDF_FILE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                // Lakukan sesuatu dengan URI file yang dipilih
                // Misalnya, simpan URI ke variabel selectedPdfUri
                selectedPdfUri = uri
                binding.txtFileName.text = getPDFFileName(uri)
            }
        }
        when (requestCode) {
            REQUEST_IMAGE_GALLERY_KK -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        selectedImageKK = imageBitmap
                        binding.imageViewKK.setImageBitmap(imageBitmap)
                    }
                }
            }

            REQUEST_IMAGE_CAMERA_KK -> {
                if (resultCode == RESULT_OK && data?.extras?.containsKey("data") == true) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    selectedImageKK = imageBitmap
                    binding.imageViewKK.setImageBitmap(imageBitmap)
                }
            }

            REQUEST_IMAGE_GALLERY_KTP -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        selectedImageKTP = imageBitmap
                        binding.imageViewKTP.setImageBitmap(imageBitmap)
                    }
                }
            }

            REQUEST_IMAGE_CAMERA_KTP -> {
                if (resultCode == RESULT_OK && data?.extras?.containsKey("data") == true) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    selectedImageKTP = imageBitmap
                    binding.imageViewKTP.setImageBitmap(imageBitmap)
                }
            }

            REQUEST_IMAGE_GALLERY_UNIT -> {
                if (resultCode == RESULT_OK) {
                    data?.data?.let { uri ->
                        val imageBitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        selectedImageUnit = imageBitmap
                        binding.imageViewUnit.setImageBitmap(imageBitmap)
                    }
                }
            }

            REQUEST_IMAGE_CAMERA_UNIT -> {
                if (resultCode == RESULT_OK && data?.extras?.containsKey("data") == true) {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    selectedImageUnit = imageBitmap
                    binding.imageViewUnit.setImageBitmap(imageBitmap)
                }
            }
        }
    }

    private fun getFileBase64(uri: Uri): String {
        val inputStream = contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    class RupiahTextWatcher(private val editText: EditText) : TextWatcher {

        private val currencyFormat = NumberFormat.getCurrencyInstance(Locale("id", "ID"))

        init {
            currencyFormat.maximumFractionDigits = 0
        }

        override fun afterTextChanged(s: Editable?) {
            editText.removeTextChangedListener(this)

            val parsed = parseToLong(s.toString())
            val formatted = currencyFormat.format(parsed)

            editText.setText(formatted)
            editText.setSelection(formatted.length)

            editText.addTextChangedListener(this)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // Not needed
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // Not needed
        }

        private fun parseToLong(value: String): Long {
            val cleanString = value.replace(Regex("[^\\d]"), "")
            return if (cleanString.isNotEmpty()) cleanString.toLong() else 0
        }
    }

    fun getStringImage(bmp: Bitmap?): String {
        val baos = ByteArrayOutputStream()
        bmp?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return encodedImage
    }
}