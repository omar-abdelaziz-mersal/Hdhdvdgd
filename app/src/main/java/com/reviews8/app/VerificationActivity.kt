package com.reviews8.app
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class VerificationActivity : AppCompatActivity() {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)
        val mode = intent.getStringExtra("mode") // register أو forgot
        val email = intent.getStringExtra("email") ?: ""
        val password = intent.getStringExtra("password") ?: ""
        val name = intent.getStringExtra("name") ?: ""

        val tvEmail = findViewById<TextView>(R.id.tvEmailShow)
        val etCode = findViewById<EditText>(R.id.etCodeVerify)
        val etNew = findViewById<EditText>(R.id.etNewPassVerify)
        val etConfirmNew = findViewById<EditText>(R.id.etConfirmNewPassVerify)
        val layoutNew = findViewById<View>(R.id.layoutNewPass)
        val layoutConfirm = findViewById<View>(R.id.layoutConfirmNewPass)
        val btn = findViewById<Button>(R.id.btnVerify)
        val tvResult = findViewById<TextView>(R.id.tvResultVerify)

        tvEmail.text = "كود مرسل إلى: $email"

        if (mode == "forgot") {
            layoutNew.visibility = View.VISIBLE
            layoutConfirm.visibility = View.VISIBLE
            btn.text = "تغيير كلمة المرور"
        } else {
            btn.text = "تأكيد وإنشاء الحساب"
        }

        btn.setOnClickListener {
            val code = etCode.text.toString().trim()
            if (code.length != 6) { tvResult.text = "اكتب كود 6 أرقام"; return@setOnClickListener }

            if (mode == "forgot") {
                val newPass = etNew.text.toString().trim()
                val confirmNew = etConfirmNew.text.toString().trim()
                if (newPass.length < 6) { tvResult.text = "كلمة المرور الجديدة قصيرة"; return@setOnClickListener }
                if (newPass != confirmNew) { tvResult.text = "إعادة كلمة المرور غير متطابقة"; return@setOnClickListener }
                tvResult.text = "جاري التغيير..."
                scope.launch {
                    try {
                        val res = withContext(Dispatchers.IO) { RetrofitClient.api.resetPassword(ResetRequest(email = email, code = code, new_password = newPass)) }
                        if (res.status == "ok") {
                            Toast.makeText(this@VerificationActivity, "تم تغيير الباسورد", Toast.LENGTH_LONG).show()
                            finish()
                        } else tvResult.text = res.msg
                    } catch (e: Exception) { tvResult.text = "خطأ: ${e.message}" }
                }
            } else {
                tvResult.text = "جاري التحقق..."
                scope.launch {
                    try {
                        val verify = withContext(Dispatchers.IO) { RetrofitClient.api.verifyCode(VerifyRequest(email = email, code = code)) }
                        if (verify.status == "ok") {
                            val reg = withContext(Dispatchers.IO) { RetrofitClient.api.register(RegisterRequest(email = email, password = password, name = name)) }
                            if (reg.status == "ok") {
                                Toast.makeText(this@VerificationActivity, "تم إنشاء الحساب", Toast.LENGTH_LONG).show()
                                finish()
                            } else tvResult.text = reg.msg
                        } else tvResult.text = "كود غلط"
                    } catch (e: Exception) { tvResult.text = "خطأ: ${e.message}" }
                }
            }
        }
    }
}
