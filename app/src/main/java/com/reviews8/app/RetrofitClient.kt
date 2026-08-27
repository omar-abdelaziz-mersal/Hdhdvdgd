package com.reviews8.app
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class RegisterActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmailReg)
        val etPass = findViewById<EditText>(R.id.etPassReg)
        val btn = findViewById<Button>(R.id.btnRegister)
        val tv = findViewById<TextView>(R.id.tvResultReg)

        btn.setOnClickListener {
            val name = etName.text.toString().trim()
            val email = etEmail.text.toString().trim()
            val pass = etPass.text.toString().trim()
            if (name.isEmpty() || email.isEmpty() || pass.length < 6) { tv.text = "الاسم والايميل وباسورد 6 حروف على الأقل"; return@setOnClickListener }
            tv.text = "جاري الإنشاء..."
            scope.launch {
                try {
                    val res = withContext(Dispatchers.IO) { RetrofitClient.api.register(RegisterRequest(email = email, password = pass, name = name)) }
                    if (res.status == "ok") {
                        Toast.makeText(this@RegisterActivity, "تم إنشاء الحساب", Toast.LENGTH_LONG).show()
                        finish()
                    } else tv.text = res.msg ?: "موجود قبل كده"
                } catch (e: Exception) { tv.text = "خطأ: ${e.message}" }
            }
        }
    }
}
