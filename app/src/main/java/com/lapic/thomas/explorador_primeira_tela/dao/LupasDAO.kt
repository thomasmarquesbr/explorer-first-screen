package com.lapic.thomas.explorador_primeira_tela.dao

import android.content.Context

import com.example.thomas.explorador_segunda_tela.model.Lupa
import com.lapic.thomas.explorador_primeira_tela.R

class LupasDAO(val context: Context) {

    private val qtdeTotalLupas = 8
    var lupas: MutableList<Lupa> = mutableListOf()

    init {
        val  resIdImage = context.resources
                .getIdentifier("ic_launcher", "mipmap", context.packageName)
        var lupa = Lupa("Sistema métrico",
                "O sistema métrico atual surgiu durante a Revolução Francesa (1789-1799) por causa das dificuldades na indústria e no comércio diante da grande variação dos padrões de medidas.",
                R.mipmap.foto1_sistema_metrico,
                R.mipmap.foto1_sistema_metrico)
        lupas.add(lupa)
        lupa = Lupa("Geografia",
                "Eratóstenes dividiu o mundo em paralelos e meridianos e seus estudos resultaram na obra “Geografia” (em grego, “descrição da terra”), livro que inaugurou esta ciência.\nOs paralelos mais importantes além do Equador são os dois trópicos e os dois círculos polares, definidos pela máxima e mínima incidência do sol ao longo do ano.\nMeridiano (do latim, “meridies”) é a “linha do meio-dia”. O planeta foi dividido em 24 meridianos, um para cada hora do dia, totalizando 24 fusos horários.",
                R.mipmap.foto1_geografia,
                R.mipmap.foto1_geografia)
        lupas.add(lupa)
        lupa = Lupa("Solstício de verão",
                "No solstício de verão, a Terra atinge sua declinação máxima e começa a retornar, o que aumenta o tempo de exposição ao sol e produz o dia mais longo do ano.\nO solstício de verão do hemisfério Norte ocorre junto com o solstício de inverno do hemisfério Sul (dia mais curto e noite mais longa do ano) e vice-versa.",
                R.mipmap.foto1_solsticio,
                R.mipmap.foto1_solsticio)
        lupas.add(lupa)
        lupa = Lupa("Eratóstenes",
                "Eratóstenes era matemático, gramático, poeta, geógrafo, bibliotecário e astrônomo. - Cirene, 276 a.C. / Alexandria, 194 a.C.",
                R.mipmap.foto1_eratostenes,
                R.mipmap.foto1_eratostenes)
        lupas.add(lupa)
        lupa = Lupa("Terra redonda",
                "Pitágoras e os seguidores da escola pitagórica já tinham a crença de que a Terra era redonda, mas foi Eratóstenes quem produziu a prova definitiva.",
                R.mipmap.foto1_terra_redonda,
                R.mipmap.foto1_terra_redonda)
        lupas.add(lupa)
        lupa = Lupa("Alternos internos",
                "Num sistema de duas retas paralelas cortadas por uma terceira, os ângulos alternos-internos (64° e 116°) são sempre geometricamente iguais entre si.",
                R.mipmap.foto1_alternos_internos,
                R.mipmap.foto1_alternos_internos)
        lupas.add(lupa)
        lupa = Lupa("Arco de ângulo",
                "A medida angular (em graus) de um arco AB é a medida do ângulo central AÔB (em graus). Com isso, o grau passou a ser uma unidade de medida também para os arcos.",
                R.mipmap.foto1_arco_angulo,
                R.mipmap.foto1_arco_angulo)
        lupas.add(lupa)
        lupa = Lupa("       360 graus",
                "Em 4 mil a.C. acreditava-se que o Sol girava em torno da Terra descrevendo uma órbita circular perfeita em 360 dias. Cada dia (1/360) era equivalente a um arco de 1 grau (1°).",
                R.mipmap.foto1_360,
                R.mipmap.foto1_360)
        lupas.add(lupa)
        lupa = Lupa("Bematistas",
                "Os bematistas eram treinados para fazer “medida em marcha” e calcular longas distâncias. Alexandre “o Grande” utilizava este método para mensurar a expansão do seu império.",
                R.mipmap.foto1_bematistas,
                R.mipmap.foto1_bematistas)
        lupas.add(lupa)
    }

    fun addLupa() {
        val resIdImage = context.resources
                .getIdentifier("ic_launcher", "mipmap", context.packageName)
        val lupa = Lupa("Android custom dialog example!",
                "Lorem ipsum blandit est netus ultrices lacus vulputate pulvinar, arcu nulla tempus nulla quisque convallis lobortis, et cubilia dui accumsan varius sollicitudin at. tortor arcu tempor at in libero urna aliquam laoreet taciti quisque tempus, praesent ligula ante molestie auctor curabitur vehicula ultricies consectetur vivamus egestas, placerat ut massa dictum potenti semper ac magna odio conubia. libero inceptos netus justo litora fusce lectus ante, per eu placerat orci luctus gravida, quisque conubia quam eu vulputate tincidunt. per mauris nisl tristique id habitant ultricies, fames curae lacinia massa dictum ad, vitae cursus enim vel magna. Turpis aliquam massa ad porta enim fusce, aliquet eros eget commodo nam integer eu, vehicula feugiat tortor elit consectetur. diam nisl feugiat himenaeos erat conubia metus suspendisse fames consequat sodales quisque habitasse, inceptos nisl aptent proin facilisis iaculis eget aliquet nostra habitant sociosqu. aptent ut nostra morbi consectetur conubia donec duis cursus libero, habitasse curae sed tempus lectus porttitor sit facilisis, donec conubia praesent lacinia augue himenaeos pharetra malesuada. habitant himenaeos imperdiet gravida sociosqu felis lacinia eget consectetur congue, dolor nostra consequat ac mi et ante lacinia. ut lectus lobortis nisi hac iaculis interdum donec senectus, phasellus sociosqu himenaeos iaculis a tempor sollicitudin, nibh lobortis ac justo ut in non. ",
                resIdImage,
                resIdImage)
        lupas.add(lupa)
    }

    fun remove(lupa: Lupa) {

    }

}