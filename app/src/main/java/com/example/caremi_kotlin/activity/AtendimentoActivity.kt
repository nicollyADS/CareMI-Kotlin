package com.example.caremi_kotlin.activity

import AtendimentoRepository
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ListView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.model.Atendimento

class AtendimentoActivity : Activity() {

    private lateinit var atendimentoRepository: AtendimentoRepository
    private lateinit var atendimentos: List<Atendimento>
    private lateinit var listViewAtendimento: ListView
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.atendimento)

        listViewAtendimento = findViewById(R.id.listViewAtendimento)
        sharedPreferences = getSharedPreferences("AtendimentoPrefs", Context.MODE_PRIVATE)
        atendimentoRepository = AtendimentoRepository()

        atualizarListaAtendimentos(listViewAtendimento)

        listViewAtendimento.setOnItemLongClickListener { _, _, position, _ ->
            val atendimentoSelecionado = atendimentos[position]
            abrirTelaDeEdicao(atendimentoSelecionado)
            true
        }

        listViewAtendimento.setOnItemClickListener { _, _, position, _ ->
            val atendimentoSelecionado = atendimentos[position]
            confirmarExclusao(atendimentoSelecionado)
        }

        carregarUltimosDados()
    }

    private fun carregarUltimosDados() {
        val descricao = sharedPreferences.getString("descricao", "")
        val dias = sharedPreferences.getString("dias", "")
        val habito = sharedPreferences.getString("habito", "")
        val sono = sharedPreferences.getString("sono", "")
        val hereditario = sharedPreferences.getString("hereditario", "")
        val data = sharedPreferences.getString("data", "")

        if (!descricao.isNullOrEmpty() || !dias.isNullOrEmpty() || !habito.isNullOrEmpty()) {
            Toast.makeText(this, "Dados do último atendimento editado carregados.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarListaAtendimentos(listView: ListView) {
        atendimentoRepository.buscarAtendimentos { atendimentos, erro ->
            if (erro != null) {
                runOnUiThread {
                    Toast.makeText(this, "Erro ao buscar atendimentos: $erro", Toast.LENGTH_SHORT).show()
                }
            } else {
                this.atendimentos = atendimentos ?: emptyList()

                val listaAtendimentosStr = this.atendimentos.map {
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
        val editData = dialogView.findViewById<EditText>(R.id.editDataEnvio)

        editDescricao.setText(atendimento.descricao)
        editDias.setText(atendimento.dias)
        editHabito.setText(atendimento.habito)
        editSono.setText(atendimento.tempoSono)
        editHereditario.setText(atendimento.hereditario)
        editData.setText(atendimento.dataEnvio)

        AlertDialog.Builder(this)
            .setTitle("Editar Atendimento")
            .setView(dialogView)
            .setPositiveButton("Salvar") { _, _ ->
                atendimento.descricao = editDescricao.text.toString()
                atendimento.dias = editDias.text.toString()
                atendimento.habito = editHabito.text.toString()
                atendimento.tempoSono = editSono.text.toString()
                atendimento.hereditario = editHereditario.text.toString()
                atendimento.dataEnvio = editData.text.toString()

                salvarDadosNoSharedPreferences(atendimento)

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

    private fun salvarDadosNoSharedPreferences(atendimento: Atendimento) {
        val editor = sharedPreferences.edit()
        editor.putString("descricao", atendimento.descricao)
        editor.putString("dias", atendimento.dias)
        editor.putString("habito", atendimento.habito)
        editor.putString("sono", atendimento.tempoSono)
        editor.putString("hereditario", atendimento.hereditario)
        editor.putString("data", atendimento.dataEnvio)
        editor.apply()
    }

    private fun confirmarExclusao(atendimento: Atendimento) {
        AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Você realmente deseja excluir este atendimento?")
            .setPositiveButton("Sim") { _, _ ->
                atendimento.id?.let { id ->
                    atendimentoRepository.excluirAtendimento(id) { sucesso, erro ->
                        if (sucesso) {
                            atualizarListaAtendimentos(listViewAtendimento)
                            Toast.makeText(this, "Atendimento excluído com sucesso", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Erro ao excluir atendimento: $erro", Toast.LENGTH_SHORT).show()
                        }
                    }
                } ?: run {
                    Toast.makeText(this, "ID do atendimento não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Não", null)
            .show()
    }
}
