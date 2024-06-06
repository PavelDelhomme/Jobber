package com.delhomme.jobber.Api.Repository

import android.content.Context
import android.util.Log
import com.delhomme.jobber.Api.DjangoApi.TokenResponse
import com.delhomme.jobber.Api.LocalApi.LocalStorageManager
import com.delhomme.jobber.Api.LoginResponse
import com.delhomme.jobber.SignUser.User
import com.delhomme.jobber.SignUser.UserProfile
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
class UserRepository(context: Context) : BaseDataRepository<User>(context, "users") {

    private val userService = RetrofitClient.createService(UserProfileApi::class.java)
    init {
        LocalStorageManager.initialize(context)
    }

    override fun updateOrAddItem(mutableItems: MutableList<User>, item: User) {
        val index = mutableItems.indexOfFirst { it.id == item.id }
        if (index != -1) {
            mutableItems[index] = item
        } else {
            mutableItems.add(item)
        }
        saveItemsToPrefs(mutableItems)
    }
    fun loginUser(username: String, password: String, callback: Callback<LoginResponse>) {
        val loginInfo = mapOf("username" to username, "password" to password)
        Log.d("UserRepository", "loginUser : loginInfo : $loginInfo")
        userService.loginUser(loginInfo).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d("UserRepository", "Réponse reçue: ${response.body()}")
                if (response.isSuccessful) {
                    response.body()?.let {
                        val token = it.access
                        val refreshToken = it.refresh

                        if (token != null && refreshToken != null) {
                            LocalStorageManager.saveJWT(token)
                            LocalStorageManager.saveRefreshToken(refreshToken)
                            callback.onResponse(call, response)
                        } else {
                            Log.e("UserRepository", "Token ou Refresh Token manquant")
                            // Informer l'utilisateur de l'erreur
                            callback.onFailure(call, RuntimeException("Token ou Refresh Token manquant"))
                        }
                    } ?: run {
                        Log.e("UserRepository", "Réponse vide du serveur")
                        callback.onFailure(call, RuntimeException("Réponse vide du serveur"))
                    }
                } else {
                    Log.e("UserRepository", "Échec de la connexion: ${response.message()}")
                    callback.onFailure(call, RuntimeException("Échec de la connexion: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("UserRepository", "Erreur réseau: ${t.message}")
                callback.onFailure(call, t)
            }
        })
    }


    fun registerUser(username: String, password: String, callback: Callback<LoginResponse>) {
        val userProfile = UserProfile(username, password)
        Log.d("registerUser UserRepository", "registerUser : userProfile : $userProfile")
        userService.createUserProfile(userProfile).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("UserRepository", "Token reçu: ${it.access}")
                        LocalStorageManager.saveJWT(it.access!!)
                        callback.onResponse(call, response)
                    }
                } else {
                    Log.e("UserRepository", "Echec de l'inscription: ${response.message()}")
                    callback.onFailure(call, RuntimeException("Failed to register: ${response.message()}"))
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e("UserRepository", "Erreur réseau: ${t.message}")
                callback.onFailure(call, t)
            }
        })
    }

    private fun refreshToken(refreshToken: String, callback: Callback<TokenResponse>) {
        val params = mapOf("refresh" to refreshToken)
        userService.refreshToken(params).enqueue(callback)
    }
}