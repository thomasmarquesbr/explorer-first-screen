package com.lapic.thomas.explorador_primeira_tela.sync

import android.os.CountDownTimer
import android.util.Log
import com.lapic.thomas.explorador_primeira_tela.model.Color
import com.lapic.thomas.explorador_primeira_tela.network.MulticastGroup
import org.json.JSONObject
import java.net.URLEncoder

class Sincronizador(val multicastGroup: MulticastGroup,
                    var tempoAtual: Int = 0,
                    val duracaoTotal: Int) {

    var id = 0
    lateinit var countDownTimer: CountDownTimer
    var isRuning = false
    set(value) {
        if (value) {
            iniciaHandler()
        } else {
            countDownTimer.cancel()
        }
    }

//    private var ancoras: HashMap<Int, String> = hashMapOf(
//            19 to Color.RED.name,
//            23 to Color.GREEN.name,
//            47 to Color.YELLOW.name,
//            57 to Color.BLUE.name,
//            82 to Color.RED.name,
//            90 to Color.GREEN.name,
//            95 to Color.YELLOW.name,
//            99 to Color.BLUE.name,
//            115 to Color.RED.name)

    private var ancoras = listOf(19, 23, 47, 57, 82, 90, 95, 99, 115)
    private val colors = listOf(Color.RED.name,
            Color.GREEN.name,
            Color.YELLOW.name,
            Color.BLUE.name,
            Color.RED.name,
            Color.GREEN.name,
            Color.YELLOW.name,
            Color.BLUE.name,
            Color.RED.name)

    private fun iniciaHandler() {
        countDownTimer =  object : CountDownTimer(((duracaoTotal - tempoAtual) * 1000).toLong(), 1000) {
            override fun onFinish() {
                Log.e("Sincronizador", "Finalizado")
            }
            override fun onTick(millisUntilFinished: Long) {
                tempoAtual++
                ancoras.forEachIndexed { i, it ->
                    if (it == tempoAtual) {
                        enviaMensagemDeInteratividadeParaSegundaTela(i, colors[i])
                        id++
                    }
                }

            }
        }.start()
    }

    private fun enviaMensagemDeInteratividadeParaSegundaTela(id: Int, color: String) {
        val jsonObject = JSONObject()
        jsonObject.put("time", tempoAtual)
        jsonObject.put("id", id)
        jsonObject.put("START", color)
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
    }

}