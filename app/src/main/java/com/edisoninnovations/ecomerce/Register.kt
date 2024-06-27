package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.Toast
import com.edisoninnovations.ecomerce.model.RegisterRequest
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.utils.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Register : AppCompatActivity() {
    private lateinit var emailLayout: TextInputLayout
    private lateinit var nameLayout: TextInputLayout
    private lateinit var lastNameLayout: TextInputLayout
    private lateinit var passwordLayout: TextInputLayout
    private lateinit var password2Layout: TextInputLayout
    private lateinit var btnRegister: Button
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadingDialog = LoadingDialog(this)

        emailLayout = findViewById(R.id.email_input_layout)
        nameLayout = findViewById(R.id.name_input_layout)
        lastNameLayout = findViewById(R.id.adress_input_layout)
        passwordLayout = findViewById(R.id.password_input_layout)
        password2Layout = findViewById(R.id.password2_input_layout)
        btnRegister = findViewById(R.id.btnRegister)

        emailLayout.editText?.addTextChangedListener(textWatcher)
        nameLayout.editText?.addTextChangedListener(textWatcher)
        lastNameLayout.editText?.addTextChangedListener(textWatcher)
        passwordLayout.editText?.addTextChangedListener(textWatcher)
        password2Layout.editText?.addTextChangedListener(textWatcher)

        btnRegister.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                register()
            }
        }
    }

    private suspend fun register() {
        loadingDialog.startLoading()
        try {
            val nameValue = nameLayout.editText?.text.toString()
            val emailValue = emailLayout.editText?.text.toString()
            val passwordValue = passwordLayout.editText?.text.toString()
            val addressValue = lastNameLayout.editText?.text.toString()
            val roleValue = "user"

            val response = RetrofitClient.instance.register(
                RegisterRequest(nameValue, emailValue, passwordValue, roleValue, addressValue)
            )

            Toast.makeText(this@Register, "Registro exitoso", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Register, Login::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("User already exists") == true -> "El usuario ya existe"
                e.message?.contains("Network error") == true -> "Error de red"
                else -> e.message
            }

            Toast.makeText(this@Register, errorMessage, Toast.LENGTH_SHORT).show()
        } finally {
            loadingDialog.isDismiss()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val isFormValid = validateForm()
            btnRegister.isEnabled = isFormValid
        }

        private fun validateForm(): Boolean {
            // Validar el correo electrónico
            val email = emailLayout.editText?.text.toString()
            val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
            if (!isEmailValid) {
                emailLayout.error = "Correo electrónico no válido"
            } else {
                emailLayout.error = null
            }

            // Validar nombre
            val name = nameLayout.editText?.text.toString()
            if (name.isEmpty()) {
                nameLayout.error = "El nombre no puede estar vacío"
            } else {
                nameLayout.error = null
            }

            // Validar apellido
            val lastName = lastNameLayout.editText?.text.toString()
            if (lastName.isEmpty()) {
                lastNameLayout.error = "El apellido no puede estar vacío"
            } else {
                lastNameLayout.error = null
            }

            // Validar la longitud de la contraseña
            val password = passwordLayout.editText?.text.toString()
            val password2 = password2Layout.editText?.text.toString()
            val isPasswordLengthValid = password.length >= 6
            if (!isPasswordLengthValid) {
                passwordLayout.error = "La contraseña debe tener al menos 6 caracteres"
            } else {
                passwordLayout.error = null
            }

            // Validar que las contraseñas coincidan
            val doPasswordsMatch = password == password2
            if (!doPasswordsMatch) {
                password2Layout.error = "Las contraseñas no coinciden"
            } else {
                password2Layout.error = null
            }

            return isEmailValid && name.isNotEmpty() && lastName.isNotEmpty() && isPasswordLengthValid && doPasswordsMatch
        }
    }
}
