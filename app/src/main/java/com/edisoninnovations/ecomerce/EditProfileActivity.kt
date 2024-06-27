package com.edisoninnovations.ecomerce

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.edisoninnovations.ecomerce.databinding.ActivityEditProfileBinding
import com.edisoninnovations.ecomerce.model.UpdateUserProfile
import com.edisoninnovations.ecomerce.request.RetrofitClient
import com.edisoninnovations.ecomerce.utils.LoadingDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var loadingDialog: LoadingDialog

    private var originalName: String? = null
    private var originalEmail: String? = null
    private var originalAddress: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadingDialog = LoadingDialog(this)

        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)
        val userId = sharedPreferences.getInt("user_id", -1)

        if (token != null && userId != -1) {
            // Fetch and display user data
            CoroutineScope(Dispatchers.Main).launch {
                try {
                    loadingDialog.startLoading()
                    val userProfile = RetrofitClient.instance.getProfile("Bearer $token")
                    originalName = userProfile.name
                    originalEmail = userProfile.email
                    originalAddress = userProfile.address

                    binding.editTextName.setText(originalName)
                    binding.editTextEmail.setText(originalEmail)
                    binding.editTextAddress.setText(originalAddress)
                } catch (e: Exception) {
                    // Handle error
                } finally {
                    loadingDialog.isDismiss()
                }
            }
        }

        binding.editTextName.addTextChangedListener(textWatcher)
        binding.editTextEmail.addTextChangedListener(textWatcher)
        binding.editTextAddress.addTextChangedListener(textWatcher)
        binding.editTextPassword.addTextChangedListener(textWatcher)

        binding.buttonSave.setOnClickListener {
            showConfirmationDialog(token, userId)
        }
    }

    private fun showConfirmationDialog(token: String?, userId: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmación")
        builder.setMessage("¿Está seguro de que desea guardar los cambios?")
        builder.setPositiveButton("Sí") { dialog, _ ->
            CoroutineScope(Dispatchers.Main).launch {
                updateUserProfile(token, userId)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private suspend fun updateUserProfile(token: String?, userId: Int) {
        loadingDialog.startLoading()
        try {
            val password = binding.editTextPassword.text.toString().takeIf { it.isNotEmpty() }

            val updatedUserProfile = UpdateUserProfile(
                name = binding.editTextName.text.toString().takeIf { it.isNotEmpty() },
                email = binding.editTextEmail.text.toString().takeIf { it.isNotEmpty() },
                address = binding.editTextAddress.text.toString().takeIf { it.isNotEmpty() },
                password = password // Opcional
            )

            RetrofitClient.instance.updateUser("Bearer $token", userId, updatedUserProfile)

            Toast.makeText(this@EditProfileActivity, "Perfil actualizado con éxito", Toast.LENGTH_SHORT).show()

            // Redirigir según si la contraseña fue cambiada o no
            if (password != null) {
                // Si la contraseña fue cambiada, redirigir a la pantalla de inicio de sesión
                val intent = Intent(this@EditProfileActivity, Login::class.java)
                startActivity(intent)
                setResult(RESULT_OK)  // Indicar que se han realizado cambios
                finish()
            } else {
                // Si no se cambió la contraseña, simplemente terminar la actividad
                setResult(RESULT_OK)  // Indicar que se han realizado cambios
                finish()
            }

        } catch (e: Exception) {
            val errorMessage = when {
                e.message?.contains("Network error") == true -> "Error de red"
                else -> e.message
            }
            Toast.makeText(this@EditProfileActivity, errorMessage, Toast.LENGTH_SHORT).show()
        } finally {
            loadingDialog.isDismiss()
        }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val isFormChanged = isFormChanged()
            binding.buttonSave.isEnabled = isFormChanged
        }

        private fun isFormChanged(): Boolean {
            val name = binding.editTextName.text.toString()
            val email = binding.editTextEmail.text.toString()
            val address = binding.editTextAddress.text.toString()
            val password = binding.editTextPassword.text.toString()

            val isPasswordLengthValid = password.isEmpty() || password.length >= 6
            if (password.isNotEmpty() && !isPasswordLengthValid) {
                binding.editTextPassword.error = "La contraseña debe tener al menos 6 caracteres"
            } else {
                binding.editTextPassword.error = null
            }

            return (name != originalName || email != originalEmail || address != originalAddress || password.isNotEmpty()) && isPasswordLengthValid
        }
    }
}
