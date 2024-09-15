package com.example.caremi_kotlin.activity

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.repository.ExameRepository
import com.example.caremi_kotlin.model.Exame

class ExameActivity : Activity() {

    private lateinit var exameRepository: ExameRepository

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.exames)

        val listViewExames = findViewById<ListView>(R.id.listViewExames)

        exameRepository = ExameRepository()

        atualizarListaExames(listViewExames)
    }

    private fun atualizarListaExames(listView: ListView) {
        exameRepository.buscarExames { exames, erro ->
            runOnUiThread {
                if (erro != null) {
                    Toast.makeText(this, "Erro ao buscar exames: $erro", Toast.LENGTH_SHORT).show()
                } else {
                    val listaExamesStr = exames?.map {
                        "Data: ${it.data}\nHora: ${it.hora}\nDescrição: ${it.descricao}"
                    } ?: emptyList()

                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaExamesStr)
                    listView.adapter = adapter
                }
            }
        }
    }
}
