package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import com.edisoninnovations.ecomerce.model.LoginRequest
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.utils.LoadingDialog
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {
    private lateinit var emailLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var btnLogin: Button
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        val emailErrorText: TextView = findViewById(R.id.email_error_text)
        loadingDialog = LoadingDialog(this)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val btn: TextView = findViewById(R.id.register_link)
        btn.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        emailLayout = findViewById(R.id.email_input_layout)
        passwordLayout = findViewById(R.id.password_input_layout)
        btnLogin = findViewById(R.id.btnLogin)

        emailLayout.editText?.addTextChangedListener(textWatcher)
        passwordLayout.editText?.addTextChangedListener(textWatcher)

        emailLayout.editText?.addTextChangedListener {
            val email = emailLayout.editText?.text.toString()
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailErrorText.visibility = View.GONE
            } else {
                emailErrorText.text = "Correo electrónico no válido"
                emailErrorText.visibility = View.VISIBLE
            }
        }

        btnLogin.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                signIn()
            }
        }
    }

    private suspend fun signIn() {
        loadingDialog.startLoading()
        try {
            val emailValue = emailLayout.editText?.text.toString()
            val passwordValue = passwordLayout.editText?.text.toString()

            val response = RetrofitClient.instance.login(LoginRequest(emailValue, passwordValue))

            // Guarda el token JWT en SharedPreferences
            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("token", response.token)
            editor.putString("role", response.role)
            editor.apply()

            Toast.makeText(this@Login, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Invalid login credentials") == true -> "Credenciales de inicio de sesión inválidas"
                e.message?.contains("Network error") == true -> "Error de red"
                e.message?.contains("Email not confirmed") == true -> "Por favor confirma tu correo electrónico"
                else -> e.message
            }

            Toast.makeText(this@Login, errorMessage, Toast.LENGTH_SHORT).show()
            println("erro$$$$"+errorMessage)
        } finally {
            loadingDialog.isDismiss()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            // Validar el correo electrónico
            val email = emailLayout.editText?.text.toString()
            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isEmailValid) {
                emailLayout.error = "Correo electrónico no válido"
            } else {
                emailLayout.error = null
            }

            // Validar la longitud de la contraseña
            val password = passwordLayout.editText?.text.toString()
            val isPasswordLengthValid = password.length >= 6
            if (!isPasswordLengthValid) {
                passwordLayout.error = "La contraseña debe tener al menos 6 caracteres"
            } else {
                passwordLayout.error = null
            }

            btnLogin.isEnabled = isEmailValid && isPasswordLengthValid
        }
    }
}
