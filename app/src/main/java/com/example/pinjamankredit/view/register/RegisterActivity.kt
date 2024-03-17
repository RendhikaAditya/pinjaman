package com.example.pinjamankredit.view.register

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.DatePicker
import com.example.pinjamankredit.R
import com.example.pinjamankredit.databinding.ActivityRegisterBinding
import com.example.pinjamankredit.view.admin.main.MainActivity
import java.util.Calendar
import java.util.Date
import java.util.Locale

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var calendar: Calendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        calendar = Calendar.getInstance()

        setupListener()
    }

    private fun setupListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnRegister.setOnClickListener { finish() }
        binding.btnMasuk.setOnClickListener { finish() }

        binding.tglLahir.setOnClickListener { showDatePickerDialog() }

    }

    private fun showDatePickerDialog() {
        val datePicker = DatePickerDialog(
            this,
            { _: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
                // Do something with the selected date
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, month, dayOfMonth)

                // Example: You can display the selected date
                binding.tglLahir.text = formatDate(selectedDate.time)
            },
            calendar.get(Calendar.YEAR), // Initial year selection
            calendar.get(Calendar.MONTH), // Initial month selection
            calendar.get(Calendar.DAY_OF_MONTH) // Initial day selection
        )

        datePicker.show()
    }

    private fun formatDate(date: Date): String {
        val format = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }
}