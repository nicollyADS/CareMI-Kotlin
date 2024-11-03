package com.example.caremi_kotlin.repository

import android.util.Log
import com.example.caremi_kotlin.model.Atendimento
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class AtendimentoRepository {

    private val BASE_URL = "http://10.0.2.2:8080/atendimentos"
    private val cliente = OkHttpClient()
    private val gson = Gson()

    fun buscarAtendimentos(callback: (List<Atendimento>?, String?) -> Unit) {
        val request = Request.Builder()
            .url(BASE_URL)
            .get()
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ATENDIMENTO_REPOSITORY", "Erro ao buscar atendimentos: ${e.message}")
                callback(null, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                Log.i("ATENDIMENTO_REPOSITORY", "Resposta: $respostaBody")

                if (response.isSuccessful && !respostaBody.isNullOrEmpty()) {
                    val type = object : TypeToken<List<Atendimento>>() {}.type
                    val listaAtendimentos: List<Atendimento> = gson.fromJson(respostaBody, type)
                    callback(listaAtendimentos, null)
                } else {
                    callback(emptyList(), "Nenhum atendimento encontrado")
                }
            }
        })
    }

    fun gravarAtendimento(atendimento: Atendimento, callback: (Boolean, String?) -> Unit) {
        val atendimentoJson = gson.toJson(atendimento)
        val requestBody = atendimentoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url(BASE_URL)
            .post(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ATENDIMENTO_REPOSITORY", "Erro ao gravar atendimento: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("ATENDIMENTO_REPOSITORY", "Atendimento gravado com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("ATENDIMENTO_REPOSITORY", "Erro ao gravar atendimento: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }

    fun editarAtendimento(atendimento: Atendimento, callback: (Boolean, String?) -> Unit) {
        val atendimentoJson = gson.toJson(atendimento)
        val requestBody = atendimentoJson.toRequestBody("application/json".toMediaType())

        val request = Request.Builder()
            .url("$BASE_URL/${atendimento.id}") // Assumindo que o ID do atendimento seja usado na URL para edição
            .put(requestBody)
            .build()

        cliente.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("ATENDIMENTO_REPOSITORY", "Erro ao editar atendimento: ${e.message}")
                callback(false, e.message)
            }

            override fun onResponse(call: Call, response: Response) {
                val respostaBody = response.body?.string()
                if (response.isSuccessful) {
                    Log.i("ATENDIMENTO_REPOSITORY", "Atendimento editado com sucesso. Resposta: $respostaBody")
                    callback(true, null)
                } else {
                    Log.e("ATENDIMENTO_REPOSITORY", "Erro ao editar atendimento: ${response.message}")
                    callback(false, respostaBody ?: "Erro desconhecido")
                }
            }
        })
    }
}
