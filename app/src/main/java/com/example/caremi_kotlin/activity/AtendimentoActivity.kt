package com.example.caremi_kotlin.activity

import android.app.Activity
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.repository.AtendimentoRepository

class AtendimentoActivity : Activity() {

    private lateinit var atendimentoRepository: AtendimentoRepository

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.atendimento)

        val listViewAtendimento = findViewById<ListView>(R.id.listViewAtendimento)

        atendimentoRepository = AtendimentoRepository()

        atualizarListaAtendimentos(listViewAtendimento)
    }

    private fun atualizarListaAtendimentos(listView: ListView) {
        atendimentoRepository.buscarAtendimentos { atendimentos, erro ->
            if (erro != null) {
                Toast.makeText(this, "Erro ao buscar atendimentos: $erro", Toast.LENGTH_SHORT).show()
            } else {
                val listaAtendimentosStr = atendimentos?.map {
                    "Nome: ${it.descricao}\nDias: ${it.dias}\nHábito: ${it.habito}\nSono: ${it.tempoSono}\nHereditário: ${it.hereditario}\nData: ${it.dataEnvio}"
                } ?: emptyList()

                val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAtendimentosStr)
                listView.adapter = adapter
            }
        }
    }
}
