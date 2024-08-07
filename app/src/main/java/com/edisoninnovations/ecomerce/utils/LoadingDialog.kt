package com.edisoninnovations.ecomerce.utils


import android.app.Activity
import android.app.AlertDialog
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.edisoninnovations.ecomerce.R

class LoadingDialog(val mActivity: Activity) {
    private lateinit var isDialog: AlertDialog
    private var handler: Handler? = null
    private var runnable: Runnable? = null

    fun startLoading() {
        /**set View*/
        val inflater = mActivity.layoutInflater
        val dialogView = inflater.inflate(R.layout.loading_item, null)
        /**set Dialog*/
        val builder = AlertDialog.Builder(mActivity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        isDialog = builder.create()
        isDialog.show()

        // Iniciar el contador de 6 segundos
        handler = Handler(Looper.getMainLooper())
        runnable = Runnable {
            if (isDialog.isShowing) {
                isDismiss()
                showToast("Ocurrió un error, vuelva a intentarlo de nuevo")
            }
        }
        handler?.postDelayed(runnable!!, 20000) // 6 segundos
    }

    fun isDismiss() {
        isDialog.dismiss()
        handler?.removeCallbacks(runnable!!)
    }

    private fun showToast(message: String) {
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show()
    }
}