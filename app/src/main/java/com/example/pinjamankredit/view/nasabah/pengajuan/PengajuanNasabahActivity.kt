package com.example.pinjamankredit.view.nasabah.pengajuan

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPengajuanNasabahBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModel
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModelFactory
import java.io.ByteArrayOutputStream
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
    }
    private var selectedImageKTP: Bitmap? = null
    private var selectedImageKK: Bitmap? = null
    private var selectedImageUnit: Bitmap? = null

    private lateinit var binding: ActivityPengajuanNasabahBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PengajuanNasabahViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PengajuanNasabahViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var helper: Helper

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

            btnSimpan.setOnClickListener {
                viewModel.fetchPengajuan(
                    "${sharedPreferences.getString(Constants.KEY_id)}",
                    "${getStringImage(selectedImageKTP)}",
                    "${getStringImage(selectedImageKK)}",
                    "${getStringImage(selectedImageUnit)}",
                    "${helper.convertRupiahToNumber(binding.danaPengajuan.text.toString())}",
                    "${binding.spAnsuran.selectedItem}"
                )
            }
        }
    }




    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

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