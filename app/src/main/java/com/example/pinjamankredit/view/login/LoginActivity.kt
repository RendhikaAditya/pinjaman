package com.example.pinjamankredit.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.pinjamankredit.databinding.ActivityLoginBinding
import com.example.pinjamankredit.network.ApiService
import com.example.pinjamankredit.network.Resource
import com.example.pinjamankredit.repositori.Repository
import com.example.pinjamankredit.util.Constants
import com.example.pinjamankredit.util.Helper
import com.example.pinjamankredit.util.SharedPreferences
import com.example.pinjamankredit.view.admin.main.MainActivity
import com.example.pinjamankredit.view.headUnit.main.MainHeadActivity
import com.example.pinjamankredit.view.nasabah.main.MainNasabahActivity
import com.example.pinjamankredit.view.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    private val api by lazy { ApiService.getClient() }
    private lateinit var viewModel: LoginViewModel
    private lateinit var repository: Repository
    private lateinit var viewModelFactory: LoginViewModelFactory
    lateinit var sharedPreferences: SharedPreferences
    lateinit var helper : Helper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sharedPreferences = SharedPreferences(this)

        setupListener()
        setupViewModel()
        setupObserve()
    }

    private fun setupObserve() {
        viewModel.login.observe(this, androidx.lifecycle.Observer {
            when (it) {
                is Resource.Loading -> {
                    Log.d("updatePengguna", " :: Loading ")
                }
                is Resource.Success -> {
                    Log.d("updatePengguna", " :: response :: ${it.data!!}")
                    if (it.data.sukses) {
                        sharedPreferences.putBoolean(Constants.KEY_IS_LOGIN, true)
                        sharedPreferences.put(Constants.KEY_id, "${it.data.data!!.id}")
                        sharedPreferences.put(Constants.KEY_LEVEL, "${it.data.data!!.level}")
                        sharedPreferences.put(Constants.KEY_NAMA, "${it.data.data!!.nama}")

                        if (it.data.data.level.equals("Admin")){
                            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                            finish()
                        }else if (it.data.data.level.equals("Unit Head")){
                            startActivity(Intent(this@LoginActivity, MainHeadActivity::class.java))
                            finish()
                        }else{
                            startActivity(Intent(this@LoginActivity, MainNasabahActivity::class.java))
                            finish()
                        }

                        Log.d("updatePengguna", ":: Sukses")
                        Toast.makeText(this@LoginActivity, "${it.data.data!!.level}", Toast.LENGTH_SHORT).show()
                    }else{
                        Log.d("updatePengguna", " :: Kosong")
                        Toast.makeText(this@LoginActivity, "${it.data.pesan}", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Error -> {
                    Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_SHORT).show()
                    Log.d("updatePengguna", " :: Error")
                }
            }
        })
    }
    fun isNotEmpty(email: String, password: String): Boolean {
        return email.isNotEmpty() && password.isNotEmpty()
    }

    private fun setupViewModel() {
        repository = Repository(api)
        viewModelFactory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(LoginViewModel::class.java)
    }

    private fun setupListener() {
        binding.btnLogin.setOnClickListener {
            val email = binding.email.text.toString()
            val password = binding.passwowrd.text.toString()
            if (isNotEmpty(email, password)) {
                viewModel.fetchLogin(binding.email.text.toString(), binding.passwowrd.text.toString() )
            } else {
                // Tampilkan pesan kesalahan
                if (email.isEmpty()) {
                    binding.email.error = "Email cannot be empty"
                } else {
                    binding.email.error = null
                }
                if (password.isEmpty()) {
                    binding.passwowrd.error = "Password cannot be empty"
                } else {
                    binding.passwowrd.error = null
                }
            }
        }
        binding.btnDaftar.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }
    }
}