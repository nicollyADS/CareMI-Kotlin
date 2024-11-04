package com.example.caremi_kotlin.activity

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.model.Exame
import com.example.caremi_kotlin.repository.ExameRepository
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ExameActivity : Activity() {

    private lateinit var exameRepository: ExameRepository
    private lateinit var sharedPreferences: SharedPreferences
    private val gson = Gson()

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.exames)

        val listViewExames = findViewById<ListView>(R.id.listViewExames)

        sharedPreferences = getSharedPreferences("ExamePrefs", Context.MODE_PRIVATE)

        exameRepository = ExameRepository()

        atualizarListaExames(listViewExames)
    }

    private fun atualizarListaExames(listView: ListView) {
        exameRepository.buscarExames { exames, erro ->
            runOnUiThread {
                if (erro != null) {
                    Toast.makeText(this, "Erro ao buscar exames: $erro", Toast.LENGTH_SHORT).show()
                    // Recupera os exames salvos localmente, se houver erro na busca
                    val examesSalvos = recuperarExamesDoSharedPreferences()
                    atualizarListView(listView, examesSalvos)
                } else {
                    if (exames != null) {
                        // Salva exames no SharedPreferences
                        salvarExamesNoSharedPreferences(exames)
                        atualizarListView(listView, exames.map {
                            "Data: ${it.data}\nHora: ${it.hora}\nDescrição: ${it.descricao}"
                        })
                    }
                }
            }
        }
    }

    private fun salvarExamesNoSharedPreferences(exames: List<Exame>) {
        val editor = sharedPreferences.edit()
        val jsonExames = gson.toJson(exames)
        editor.putString("exames", jsonExames)
        editor.apply()
    }

    private fun recuperarExamesDoSharedPreferences(): List<String> {
        val jsonExames = sharedPreferences.getString("exames", null)
        return if (jsonExames != null) {
            val type = object : TypeToken<List<Exame>>() {}.type
            val exames: List<Exame> = gson.fromJson(jsonExames, type)
            exames.map { "Data: ${it.data}\nHora: ${it.hora}\nDescrição: ${it.descricao}" }
        } else {
            emptyList()
        }
    }

    private fun atualizarListView(listView: ListView, listaExamesStr: List<String>) {
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaExamesStr)
        listView.adapter = adapter
    }
}
