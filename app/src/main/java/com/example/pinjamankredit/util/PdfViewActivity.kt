package com.example.pinjamankredit.util

import android.app.DownloadManager
import android.content.Context
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityPdfViewBinding
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import java.io.BufferedInputStream
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class PdfViewActivity : AppCompatActivity() {
    private lateinit var binding :ActivityPdfViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfViewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pdfUrl = intent.getStringExtra("pdfUrl")

       loadPDF(pdfUrl+"")
        binding.btnBack.setOnClickListener { finish() }
        binding.btnDownload.setOnClickListener { downloadPDF() }
    }
    fun downloadPDF() {
        val pdfUrl = intent.getStringExtra("pdfUrl")
        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(pdfUrl)
        val request = DownloadManager.Request(downloadUri)
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
        request.setTitle("PDF Download")
        request.setDescription("Downloading PDF file...")
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "file_laporan.pdf")
        downloadManager.enqueue(request)
    }

    private fun loadPDF(url: String) {
        object : Thread() {
            override fun run() {
                try {
                    val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
                    connection.connect()
                    val inputStream: InputStream = BufferedInputStream(connection.inputStream)
                    runOnUiThread {
                        // Set the PDF from InputStream
                        binding.pdfView.fromStream(inputStream)
                            .defaultPage(0)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .onPageChange(object : OnPageChangeListener {
                                override fun onPageChanged(page: Int, pageCount: Int) {
                                    // Code to handle page change event
                                }
                            })
                            .onLoad(object : OnLoadCompleteListener {
                                override fun loadComplete(nbPages: Int) {
                                    // Code to handle load complete event
                                }
                            })
                            .scrollHandle(DefaultScrollHandle(this@PdfViewActivity))
                            .spacing(10) // in dp
                            .load()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }
}