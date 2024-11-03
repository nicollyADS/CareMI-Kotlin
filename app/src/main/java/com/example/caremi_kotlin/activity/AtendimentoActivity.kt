package com.example.caremi_kotlin.activity

import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.model.Atendimento
import com.example.caremi_kotlin.repository.AtendimentoRepository

class AtendimentoActivity : Activity() {

    private lateinit var atendimentoRepository: AtendimentoRepository
    private lateinit var atendimentos: List<Atendimento> // Campo para armazenar a lista de atendimentos
    private lateinit var listViewAtendimento: ListView // Declara como lateinit

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.atendimento)

        listViewAtendimento = findViewById(R.id.listViewAtendimento) // Inicializa a variável lateinit

        atendimentoRepository = AtendimentoRepository()

        atualizarListaAtendimentos(listViewAtendimento)

        // Listener para clique longo
        listViewAtendimento.setOnItemLongClickListener { _, _, position, _ ->
            val atendimentoSelecionado = atendimentos[position] // Acesse o atendimento selecionado
            abrirTelaDeEdicao(atendimentoSelecionado) // Abra a tela de edição
            true
        }
    }

    private fun atualizarListaAtendimentos(listView: ListView) {
        atendimentoRepository.buscarAtendimentos { atendimentos, erro ->
            if (erro != null) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar atendimentos: $erro", Toast.LENGTH_SHORT).show()
                }
            } else {
                this.atendimentos = atendimentos ?: emptyList() // Armazena a lista de atendimentos

                val listaAtendimentosStr = this.atendimentos.map { // Aqui usamos `this.atendimentos` que não é nulo
                    "Nome: ${it.descricao}\nDias: ${it.dias}\nHábito: ${it.habito}\nSono: ${it.tempoSono}\nHereditário: ${it.hereditario}\nData: ${it.dataEnvio}"
                }

                runOnUiThread {
                    val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listaAtendimentosStr)
                    listView.adapter = adapter
                }
            }
        }
    }

    private fun abrirTelaDeEdicao(atendimento: Atendimento) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_atendimento, null)

        val editDescricao = dialogView.findViewById<EditText>(R.id.editDescricao)
        val editDias = dialogView.findViewById<EditText>(R.id.editDias)
        val editHabito = dialogView.findViewById<EditText>(R.id.editHabito)
        val editSono = dialogView.findViewById<EditText>(R.id.editSono)
        val editHereditario = dialogView.findViewById<EditText>(R.id.editHereditario)
        val editDataEnvio = dialogView.findViewById<EditText>(R.id.editDataEnvio)

        editDescricao.setText(atendimento.descricao)
        editDias.setText(atendimento.dias)
        editHabito.setText(atendimento.habito)
        editSono.setText(atendimento.tempoSono)
        editHereditario.setText(atendimento.hereditario)
        editDataEnvio.setText(atendimento.dataEnvio)

        AlertDialog.Builder(this)
            .setTitle("Editar Atendimento")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                atendimento.descricao = editDescricao.text.toString()
                atendimento.dias = editDias.text.toString()
                atendimento.habito = editHabito.text.toString()
                atendimento.tempoSono = editSono.text.toString()
                atendimento.hereditario = editHereditario.text.toString()
                atendimento.dataEnvio = editDataEnvio.text.toString()

                // Chama o método de edição
                atendimentoRepository.editarAtendimento(atendimento) { sucesso, erro ->
                    if (sucesso) {
                        atualizarListaAtendimentos(listViewAtendimento)
                        Toast.makeText(this, "Atendimento editado com sucesso", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Erro ao editar atendimento: $erro", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
