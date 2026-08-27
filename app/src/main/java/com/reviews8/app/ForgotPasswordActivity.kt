package com.reviews8.app
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val etEmail = findViewById<EditText>(R.id.etEmailForgot)
        val etCode = findViewById<EditText>(R.id.etCode)
        val etNew = findViewById<EditText>(R.id.etNewPass)
        val btnSend = findViewById<Button>(R.id.btnSendCode)
        val btnReset = findViewById<Button>(R.id.btnReset)
        val tv = findViewById<TextView>(R.id.tvResultForgot)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) { tv.text = "اكتب ايميلك"; return@setOnClickListener }
            tv.text = "جاري الإرسال..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.sendCode(SendCodeRequest(email = email)) }
                    tv.text = if (res.status == "ok") "الكود اتبعت لـ $email" else res.msg
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }

        btnReset.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val code = etCode.text.toString().trim()
            val newPass = etNew.text.toString().trim()
            if (code.isEmpty() || newPass.length < 6) { tv.text = "اكتب الكود وباسورد جديد 6 حروف"; return@setOnClickListener }
            tv.text = "جاري التغيير..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.resetPassword(ResetRequest(email = email, code = code, new_password = newPass)) }
                    if (res.status == "ok") {
                        Toast.makeText(this@ForgotPasswordActivity, "تم تغيير الباسورد", Toast.LENGTH_LONG).show()
                        finish()
                    } else tv.text = res.msg ?: "كود غلط"
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }
    }
}
