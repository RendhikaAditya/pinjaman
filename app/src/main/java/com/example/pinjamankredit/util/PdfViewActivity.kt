package com.example.pinjamankredit.util

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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