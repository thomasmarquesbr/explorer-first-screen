package com.example.thomas.explorador_segunda_tela.model

import com.lapic.thomas.explorador_primeira_tela.R

enum class Tipo(val cor: String,
                val resIdLupa: Int) {

    RED("RED", R.drawable.ic_red_magnify),
    GREEN("GREEN", R.drawable.ic_green_magnify),
    YELLOW("YELLOW", R.drawable.ic_yellow_magnify),
    BLUE("BLUE", R.drawable.ic_blue_magnify);

}