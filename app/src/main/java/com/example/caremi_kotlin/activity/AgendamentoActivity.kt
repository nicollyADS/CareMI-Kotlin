package com.example.caremi_kotlin.activity

import AtendimentoRepository
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.model.Atendimento

class AgendamentoActivity : Activity() {

    private lateinit var atendimentoRepository: AtendimentoRepository
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.agendamento)

        sharedPreferences = getSharedPreferences("AgendamentoPrefs", Context.MODE_PRIVATE)

        val edtDescricao = findViewById<EditText>(R.id.edtDescricao)
        val edtDias = findViewById<EditText>(R.id.edtDias)
        val edtHabito = findViewById<EditText>(R.id.edtHabito)
        val edtSono = findViewById<EditText>(R.id.edtSono)
        val edtHereditario = findViewById<EditText>(R.id.edtHereditario)
        val edtData = findViewById<EditText>(R.id.edtData)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        edtDescricao.setText(sharedPreferences.getString("descricao", ""))
        edtDias.setText(sharedPreferences.getString("dias", ""))
        edtHabito.setText(sharedPreferences.getString("habito", ""))
        edtSono.setText(sharedPreferences.getString("sono", ""))
        edtHereditario.setText(sharedPreferences.getString("hereditario", ""))
        edtData.setText(sharedPreferences.getString("data", ""))

        atendimentoRepository = AtendimentoRepository()

        btnEnviar.setOnClickListener {
            val descricao = edtDescricao.text.toString()
            val dias = edtDias.text.toString()
            val habito = edtHabito.text.toString()
            val sono = edtSono.text.toString()
            val hereditario = edtHereditario.text.toString()
            val data = edtData.text.toString()

            val atendimento = Atendimento(null, descricao, dias, habito, sono, hereditario, data)

            with(sharedPreferences.edit()) {
                putString("descricao", descricao)
                putString("dias", dias)
                putString("habito", habito)
                putString("sono", sono)
                putString("hereditario", hereditario)
                putString("data", data)
                apply()
            }

            atendimentoRepository.gravarAtendimento(atendimento) { sucesso, mensagem ->
                runOnUiThread {
                    if (sucesso) {
                        Toast.makeText(this, "Atendimento gravado com sucesso!", Toast.LENGTH_SHORT).show()
                        edtDescricao.text.clear()
                        edtDias.text.clear()
                        edtHabito.text.clear()
                        edtSono.text.clear()
                        edtHereditario.text.clear()
                        edtData.text.clear()

                        sharedPreferences.edit().clear().apply()
                    } else {
                        Toast.makeText(this, "Erro ao gravar atendimento: $mensagem", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
