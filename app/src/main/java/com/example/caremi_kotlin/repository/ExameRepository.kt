package com.example.caremi_kotlin.repository

import android.util.Log
import com.example.caremi_kotlin.model.Exame
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import java.io.IOException

class ExameRepository {

    private val BASE_URL = "http://10.0.2.2:8080/exames"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    fun buscarExames(callback: (List<Exame>?, String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("EXAME_REPOSITORY", "Erro ao buscar exames: ${e.message}", e)
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                if (!response.isSuccessful) {
                    val errorMessage = "Erro na resposta: ${response.message}"
                    Log.e("EXAME_REPOSITORY", errorMessage)
                    callback(null, errorMessage)
                    return
                }

                val respostaBody = response.body?.string()
                Log.i("EXAME_REPOSITORY", "Resposta: $respostaBody")

                if (respostaBody.isNullOrEmpty()) {
                    callback(emptyList(), "Nenhum exame encontrado")
                    return
                }

                try {
                    val type = object : TypeToken<List<Exame>>() {}.type
                    val listaExames: List<Exame> = gson.fromJson(respostaBody, type)
                    callback(listaExames, null)
                } catch (e: Exception) {
                    Log.e("EXAME_REPOSITORY", "Erro ao processar resposta: ${e.message}", e)
                    callback(emptyList(), "Erro ao processar resposta")
                }
            }
        })
    }
}
