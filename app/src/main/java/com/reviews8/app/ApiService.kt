package com.reviews8.app
import retrofit2.http.Body
import retrofit2.http.POST

data class ApiResponse(val status: String, val msg: String?, val user: UserData?)
data class UserData(val email: String, val name: String)
data class LoginRequest(val action: String = "login", val email: String, val password: String)

interface ApiService {
    @POST("api.php")
    suspend fun login(@Body request: LoginRequest): ApiResponse
    companion object {
        const val BASE_URL = "https://reviews8.site.je/"
    }
}
