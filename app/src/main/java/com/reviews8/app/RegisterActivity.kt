package com.reviews8.app
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPass = findViewById<EditText>(R.id.etPassReg)
        val etConfirm = findViewById<EditText>(R.id.etConfirmPassReg)
        val btnSend = findViewById<Button>(R.id.btnSendCodeReg)
        val tv = findViewById<TextView>(R.id.tvResultReg)

        btnSend.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            val confirm = etConfirm.text.toString().trim()
            if (email.isEmpty() || pass.length < 6) { tv.text = "اكتب إيميل وباسورد 6 حروف"; return@setOnClickListener }
            if (pass != confirm) { tv.text = "إعادة كلمة المرور غير متطابقة"; return@setOnClickListener }
            tv.text = "جاري إرسال كود التحقق..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.sendCode(SendCodeRequest(email = email)) }
                    if (res.status == "ok") {
                        val intent = Intent(this@RegisterActivity, VerificationActivity::class.java)
                        intent.putExtra("mode", "register")
                        intent.putExtra("email", email)
                        intent.putExtra("password", pass)
                        intent.putExtra("name", email.substringBefore("@"))
                        startActivity(intent)
                        finish()
                    } else tv.text = res.msg
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }
    }
}
