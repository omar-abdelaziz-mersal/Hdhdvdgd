package com.reviews8.app
import retrofit2.http.Body
import retrofit2.http.POST

data class ApiResponse(val status: String, val msg: String?, val user: UserData?)
data class UserData(val email: String, val name: String)

data class LoginRequest(val action: String = "login", val email: String, val password: String)
data class RegisterRequest(val action: String = "register", val email: String, val password: String, val name: String)
data class SendCodeRequest(val action: String = "send_code", val email: String)
data class VerifyRequest(val action: String = "verify_code", val email: String, val code: String)
data class ResetRequest(val action: String = "reset_password", val email: String, val code: String, val new_password: String)

interface ApiService {
    @POST("api.php") suspend fun login(@Body r: LoginRequest): ApiResponse
    @POST("api.php") suspend fun register(@Body r: RegisterRequest): ApiResponse
    @POST("api.php") suspend fun sendCode(@Body r: SendCodeRequest): ApiResponse
    @POST("api.php") suspend fun verifyCode(@Body r: VerifyRequest): ApiResponse
    @POST("api.php") suspend fun resetPassword(@Body r: ResetRequest): ApiResponse
    companion object { const val BASE_URL = "https://reviews8.site.je/" }
}
