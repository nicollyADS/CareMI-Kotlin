package com.example.caremi_kotlin.model

data class Agendamento(
    val id: Long? = null,
    var descricao: String = "",
    var dias: String = "",
    var habito: String = "",
    var tempoSono: String = "",
    var hereditario: String = "",
    var dataEnvio: String = ""
)

