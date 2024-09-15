package com.example.caremi_kotlin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import com.example.caremi_kotlin.R

class HomeActivity : Activity() {

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.home)

        val btnExames = findViewById<Button>(R.id.txtExames)
        val btnAgendamento = findViewById<Button>(R.id.txtAgendamento)
        val btnAtendimento = findViewById<Button>(R.id.txtAtendimento)

        btnExames.setOnClickListener {
            val intent = Intent(this, ExameActivity::class.java)
            startActivity(intent)
        }

        btnAgendamento.setOnClickListener {
            val intent = Intent(this, AgendamentoActivity::class.java)
            startActivity(intent)
        }

        btnAtendimento.setOnClickListener {
            val intent = Intent(this, AtendimentoActivity::class.java)
            startActivity(intent)
        }
    }
}
