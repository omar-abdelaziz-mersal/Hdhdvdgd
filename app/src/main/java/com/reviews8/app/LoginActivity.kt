package com.reviews8.app
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class LoginActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPass = findViewById<EditText>(R.id.etPassword)
        val btn = findViewById<Button>(R.id.btnLogin)
        val tv = findViewById<TextView>(R.id.tvResult)
        val tvReg = findViewById<TextView>(R.id.tvRegister)
        val tvForgot = findViewById<TextView>(R.id.tvForgot)
        tvReg.setOnClickListener { startActivity(Intent(this, RegisterActivity::class.java)) }
        tvForgot.setOnClickListener { startActivity(Intent(this, ForgotPasswordActivity::class.java)) }
        btn.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            if (email.isEmpty() || pass.isEmpty()) { tv.text = "اكمل البيانات"; return@setOnClickListener }
            tv.text = "جاري الدخول..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.login(LoginRequest(email = email, password = pass)) }
                    if (res.status == "ok") {
                        getSharedPreferences("user", MODE_PRIVATE).edit().putString("email", res.user?.email).putString("name", res.user?.name).apply()
                        Toast.makeText(this@LoginActivity, "أهلا ${res.user?.name}", Toast.LENGTH_LONG).show()
                        tv.text = "تم: ${res.user?.email}"
                    } else tv.text = res.msg ?: "خطأ"
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }
    }
}
