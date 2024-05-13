package com.example.pinjamankredit.view.admin.pengajuan

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPengajuanBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.response.NasabahResponse
import com.example.pinjamankredit.response.PengajuanResponse
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.PdfViewActivity
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.nasabah.AddNasabahActivity
import com.example.pinjamankredit.view.admin.nasabah.NasabahAdapter
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModel
import com.example.pinjamankredit.view.admin.nasabah.NasabahViewModelFactory
import java.io.File

class PengajuanActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPengajuanBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: PengajuanViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: PengajuanViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: PengajuanAdapter
    lateinit var helper: Helper
    private val PERMISSION_REQUEST_CODE = 1001
    private var downloadId: Long = -1


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPengajuanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        helper = Helper()

        setupListener()
        setupViewModel()
        setupObserve()
    }

    private fun setupObserve() {
        viewModel.fetchPengajuan()
        viewModel.pengajuan.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("deleteDeviceId", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("deleteDeviceId", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        adapter.setData(it.data.data)
                        binding.rvPengajuan.visibility = View.VISIBLE
                        Log.d("deleteDeviceId", ":: Sukses = ${it.data} :: ${it.data.data.size}")
                    }else{
                        Log.d("deleteDeviceId", " :: Kosong")
                        binding.rvPengajuan.visibility = View.GONE
                    }
                }
                is Resource.Error -> {
                    Log.d("deleteDeviceId", " :: Error")
                }
            }
        })
        adapter = PengajuanAdapter(arrayListOf(),
            object : PengajuanAdapter.OnAdapterListener{
                override fun onClick(result: PengajuanResponse.Data) {
                    TODO("Not yet implemented")
                }

                override fun onClickShow(result: PengajuanResponse.Data) {
                    showPengajuanDataDialog(this@PengajuanActivity, result)
                }

                override fun onClickEdit(result: PengajuanResponse.Data) {
                }

                override fun onClickDelete(result: PengajuanResponse.Data) {

                }

            }
        )
        binding.rvPengajuan.adapter = adapter
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = PengajuanViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(PengajuanViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.downloadLayout.setOnClickListener {
            val url = "${ApiService.baseURL}laporan.php"

            val intent = Intent(this@PengajuanActivity, PdfViewActivity::class.java).putExtra("pdfUrl", url)

            startActivity(intent)
//            startDownload()
        }
        binding.openButton.setOnClickListener { openFile() }

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
        val uri = Uri.parse("${ApiService.baseURL}laporan.php")
        val request = DownloadManager.Request(uri)
        request.setDestinationInExternalPublicDir(
            Environment.DIRECTORY_DOWNLOADS,
            "laporan.xls"
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
        val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "laporan.xls")
        val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(uri, "application/pdf")
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION // Tambahkan ini untuk memberikan izin baca ke aplikasi lain
        startActivity(intent)
    }
    fun showPengajuanDataDialog(context: Context, data: PengajuanResponse.Data) {
        val dialogView = TextView(context).apply {
            // Menyiapkan teks untuk ditampilkan dalam dialog
            text = buildString {
                append("Nama         : ${data.nasabah.nama_nasabah}\n")
                append("Pengajuan    : ${helper.formatRupiah(data.dana_pinjaman_diajukan)}\n")
                append("Disetujui    : ${helper.formatRupiah(data.dana_pinjaman_diterima)}\n")
                append("Ansuran      : ${data.lama_ansuran}\n")
                append("Status       : ${data.status_pengajuan}\n")
                append("Keterangan   : ${data.keterangan}\n")
            }
            setPadding(50, 30, 50, 30)
        }

        // Membuat AlertDialog
        val dialogBuilder = AlertDialog.Builder(context)
            .setView(dialogView)
            .setTitle("Data Nasabah")
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }


}