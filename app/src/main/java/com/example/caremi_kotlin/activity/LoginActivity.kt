package com.example.caremi_kotlin.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.caremi_kotlin.R
import com.example.caremi_kotlin.repository.LoginRepository
import com.example.caremi_kotlin.model.Login

class LoginActivity : Activity() {

    private lateinit var loginRepository: LoginRepository

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.login)

        loginRepository = LoginRepository()

        val edtLogin = findViewById<EditText>(R.id.edtLogin)
        val edtSenha = findViewById<EditText>(R.id.edtSenha)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)

        btnEntrar.setOnClickListener {
            val cpf = edtLogin.text.toString().trim()
            val senha = edtSenha.text.toString().trim()

            if (cpf.isNotEmpty() && senha.isNotEmpty()) {
                val login = Login(null, cpf, senha)
                loginRepository.fazerLogin(login) { sucesso, erro ->
                    runOnUiThread {
                        if (sucesso) {
                            Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this, HomeActivity::class.java))
                        } else {
                            Toast.makeText(this, "Falha no login: $erro", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
