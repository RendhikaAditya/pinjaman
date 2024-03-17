package com.example.pinjamankredit.util

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.pinjamankredit.R

class ZoomableImageDialog(context: Context, private val imageUrl: String) : Dialog(context) {

    private lateinit var imageView: ImageView
    private lateinit var scaleGestureDetector: ScaleGestureDetector
    private lateinit var gestureDetector: GestureDetector
    private var scaleFactor = 1.0f
    private var matrix = Matrix()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_image)

        Log.d("TAG IMAGE", "onCreate:: $imageUrl ")
        imageView = findViewById(R.id.imageView)
        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
        gestureDetector = GestureDetector(context, GestureListener())
        matrix.setScale(1.0f, 1.0f)
        imageView.imageMatrix = matrix


        Glide.with(context)
            .load(imageUrl)
            .into(imageView)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            scaleFactor *= detector.scaleFactor
            scaleFactor = kotlin.math.max(0.1f, kotlin.math.min(scaleFactor, 10.0f))
            matrix.setScale(scaleFactor, scaleFactor)
            imageView.imageMatrix = matrix
            return true
        }
    }

    private inner class GestureListener : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onDoubleTap(e: MotionEvent): Boolean {
            scaleFactor = 1.0f
            matrix.setScale(scaleFactor, scaleFactor)
            imageView.imageMatrix = matrix
            return true
        }
    }
}
