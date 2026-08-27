package com.reviews8.app
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class ForgotPasswordActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        val etEmail = findViewById<EditText>(R.id.etEmailForgot)
        val btnSend = findViewById<Button>(R.id.btnSendCodeForgot)
        val tv = findViewById<TextView>(R.id.tvResultForgot)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isEmpty()) { tv.text = "اكتب إيميلك"; return@setOnClickListener }
            tv.text = "جاري الإرسال..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.sendCode(SendCodeRequest(email = email)) }
                    if (res.status == "ok") {
                        val intent = Intent(this@ForgotPasswordActivity, VerificationActivity::class.java)
                        intent.putExtra("mode", "forgot")
                        intent.putExtra("email", email)
                        startActivity(intent)
                        finish()
                    } else tv.text = res.msg
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }
    }
}
