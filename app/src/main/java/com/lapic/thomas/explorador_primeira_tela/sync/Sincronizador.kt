package com.lapic.thomas.explorador_primeira_tela.sync

import android.os.CountDownTimer
import android.util.Log
import com.lapic.thomas.explorador_primeira_tela.model.Color
import com.lapic.thomas.explorador_primeira_tela.network.MulticastGroup
import org.json.JSONObject
import java.net.URLEncoder
import kotlin.collections.HashMap

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

    private var ancoras: HashMap<Int, String> = hashMapOf(
            19 to Color.RED.name,
            23 to Color.GREEN.name,
            47 to Color.YELLOW.name,
            57 to Color.BLUE.name,
            82 to Color.RED.name,
            90 to Color.GREEN.name,
            95 to Color.YELLOW.name,
            99 to Color.BLUE.name,
            115 to Color.RED.name)

    private fun iniciaHandler() {
        val ancorasAux = HashMap(ancoras)
        ancorasAux.forEach {
            if (it.key < tempoAtual)
                ancoras.remove(it.key)
        }
        countDownTimer =  object : CountDownTimer(((duracaoTotal - tempoAtual) * 1000).toLong(), 1000) {
            override fun onFinish() {
                Log.e("Sincronizador", "Finalizado")
            }
            override fun onTick(millisUntilFinished: Long) {
                tempoAtual++
                ancoras.forEach {
                    if (it.key == tempoAtual) {
                        enviaMensagemDeInteratividadeParaSegundaTela(it)
                        id++
                    }
                }
            }
        }.start()
    }

    private fun enviaMensagemDeInteratividadeParaSegundaTela(ancora: Map.Entry<Int, String>) {
        val jsonObject = JSONObject()
        jsonObject.put("time", tempoAtual)
        jsonObject.put("id", id)
        jsonObject.put("START", ancora.value)
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
    }

}