package com.lapic.thomas.explorador_primeira_tela

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.CountDownTimer
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.Gravity.START
import android.view.Gravity.TOP
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.example.thomas.explorador_segunda_tela.model.Lupa
import com.example.thomas.explorador_segunda_tela.model.Tipo
import com.lapic.thomas.explorador_primeira_tela.dao.LupasDAO
import com.lapic.thomas.explorador_primeira_tela.view.CanvasView
import java.io.BufferedReader
import java.io.InputStreamReader

class ControladorDesenho(val context: Context,
                         val canvas: CanvasView,
                         val rootView: FrameLayout,
                         val widthDevice: Int,
                         val heightDevice: Int) {

    private var sizeImage: Int = 0
    private var countTimer = 0
    private val lupasDAO = LupasDAO(context)
    private var listCoordinateX = mutableListOf<Float>()
    private var listCoordinateY = mutableListOf<Float>()
    private var currentCoordinateX: Float = Float.MIN_VALUE
    private var currentCoordinateY: Float = Float.MIN_VALUE
    private var isDrawing = false
    private val HEIGHT_BASE = 2048
    private val WIDTH_BASE = 1536
    private var tempoAtual = 0
    private var mostrarAcima = true
    private var mostrarEsquerda = true
    private var currentCoordinateIndex = 0

    private lateinit var timer: CountDownTimer
    private lateinit var timerDrawing: CountDownTimer

    init {
        sizeImage = if (widthDevice > 1079) 120 else 60
        canvas.setOnTouchListener { v, event ->
            return@setOnTouchListener true
        }
    }

    private fun removeTodasAsLupas() {
        (context as PlayerActivity).runOnUiThread {
            val canvasView = rootView.findViewById<CanvasView>(R.id.canvas_view)
            val imageView = rootView.findViewById<ImageView>(R.id.image_view)
            val xImage = rootView.findViewById<ImageView>(R.id.x_image)
            xImage.visibility = View.INVISIBLE
            val imageHat = rootView.findViewById<ImageView>(R.id.image_hat)
            val clearButton = rootView.findViewById<Button>(R.id.clear_button)
            rootView.removeAllViews()
            rootView.addView(canvasView)
            rootView.addView(imageView)
            rootView.addView(xImage)
            rootView.addView(imageHat)
            rootView.addView(clearButton)
        }
    }

    fun inicializarDesenho(duracaoTotal: Int) {
        removeTodasAsLupas()
        lerCoordenadas()
        isDrawing = true
        tempoAtual = 0
        val size = if (listCoordinateX.size <= listCoordinateY.size) listCoordinateX.size else listCoordinateY.size
        currentCoordinateX = widthDevice * listCoordinateX[0] / WIDTH_BASE
        currentCoordinateY = heightDevice * listCoordinateY[0] / HEIGHT_BASE
        iniciarTemporizador(duracaoTotal)
        currentCoordinateIndex = 0
        (context as PlayerActivity).runOnUiThread {
            canvas.clearCanvas()
            canvas.shootEventTouch(MotionEvent.ACTION_DOWN, currentCoordinateX - 1, currentCoordinateY - 1)
            timerDrawing = object : CountDownTimer(((duracaoTotal - tempoAtual) * 1000).toLong(), 380) {
                override fun onFinish() {
                    canvas.shootEventTouch(MotionEvent.ACTION_DOWN, currentCoordinateX + 1, currentCoordinateY + 1)
                    isDrawing = false
                }
                override fun onTick(millisUntilFinished: Long) {
                    currentCoordinateX = widthDevice * listCoordinateX[currentCoordinateIndex] / WIDTH_BASE
                    currentCoordinateY = heightDevice * listCoordinateY[currentCoordinateIndex] / HEIGHT_BASE
                    moveChapeu()
                    canvas.shootEventTouch(MotionEvent.ACTION_MOVE, currentCoordinateX, currentCoordinateY)
                    currentCoordinateIndex++
                }
            }.start()
        }
    }

    private fun iniciarTemporizador(duracaoTotal: Int) {
        (context as PlayerActivity).runOnUiThread {
            timer = object : CountDownTimer((duracaoTotal * 1000).toLong(), 1000) {
                override fun onFinish() {}
                override fun onTick(millisUntilFinished: Long) {
                    countTimer++
                }
            }.start()
        }
    }

    fun pausarDesenho(currentDuration: Int) {
        tempoAtual = currentDuration
        (context as PlayerActivity).runOnUiThread {
            if (timerDrawing != null)
                timerDrawing.cancel()
            if (timer != null)
                timer.cancel()
        }
    }

    fun retomarDesenho(totalDuration: Int, currentDuration: Int) {
        tempoAtual = currentDuration
        if (totalDuration == 0)
            return

        isDrawing = true
        iniciarTemporizador(totalDuration)
        (context as PlayerActivity).runOnUiThread {
            timerDrawing = object : CountDownTimer(((totalDuration - tempoAtual) * 1000).toLong(), 380) {
                override fun onFinish() {
                    canvas.shootEventTouch(MotionEvent.ACTION_DOWN, currentCoordinateX + 1, currentCoordinateY + 1)
                    isDrawing = false
                }
                override fun onTick(millisUntilFinished: Long) {
                    currentCoordinateX = widthDevice * listCoordinateX[currentCoordinateIndex] / WIDTH_BASE
                    currentCoordinateY = heightDevice * listCoordinateY[currentCoordinateIndex] / HEIGHT_BASE
                    moveChapeu()
                    canvas.shootEventTouch(MotionEvent.ACTION_MOVE, currentCoordinateX, currentCoordinateY)
                    currentCoordinateIndex++
                }
            }.start()
        }
    }

    fun finalizarDesenho() {
        (context as PlayerActivity).runOnUiThread {
            if (isDrawing)
                timerDrawing.cancel()
            val xImage = rootView.findViewById<ImageView>(R.id.x_image)
            with(xImage) {
                visibility = View.VISIBLE
                setImageDrawable(context.getDrawable(R.mipmap.x_image))
                x = (currentCoordinateX.toInt() - sizeImage / 2).toFloat()
                y = (currentCoordinateY.toInt() - sizeImage / 2).toFloat()
            }
            val chapeu = rootView.findViewById<ImageView>(R.id.image_hat)
            chapeu.visibility = View.INVISIBLE
        }
    }

    fun exibirLupa(id: Int, corLupa: String, isOffline: Boolean) {
        val lupa = lupasDAO.lupas[id]
        lupa.tipo = Tipo.valueOf(corLupa)
        (context as PlayerActivity).runOnUiThread {
            var paramsLupa = FrameLayout.LayoutParams(80, 80)
            with(paramsLupa) {
                gravity = TOP or START
                leftMargin = currentCoordinateX.toInt() - paramsLupa.width / 2
                topMargin = currentCoordinateY.toInt() - paramsLupa.height / 2
            }
            val imageButton = ImageButton(context)
            with(imageButton) {
                tag = "lupa"
                setImageDrawable(context.getDrawable(lupa.tipo.resIdLupa))
                layoutParams = paramsLupa
                setBackgroundColor(Color.TRANSPARENT)
                setOnClickListener {
                    exibirDialog(lupa)
                }
            }
            rootView.addView(imageButton)

            val paramsTitulo = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            with(paramsTitulo) {
                gravity = START
                configuraLayoutTitulo()
            }
            val tituloLupa = TextView(context)
            with(tituloLupa) {
                tag = "tituloLupa"
                text = lupa.title
                textSize = 18F
                layoutParams = paramsTitulo
                typeface = Typeface.createFromAsset(context.assets, "font/papyrus_let_bold.ttf")
//                typeface = Typeface.DEFAULT_BOLD
                setTextColor(ContextCompat.getColor(context, R.color.orange))
            }
            rootView.addView(tituloLupa)
        }
    }

    private fun FrameLayout.LayoutParams.configuraLayoutTitulo() {
        if (mostrarAcima) {
            if (mostrarEsquerda) { // superior esquerdo
                leftMargin = currentCoordinateX.toInt() - 280
                topMargin = currentCoordinateY.toInt() - 100
                mostrarEsquerda = !mostrarEsquerda
            } else { //superior direito
                leftMargin = currentCoordinateX.toInt() + 20
                topMargin = currentCoordinateY.toInt() - 100
                mostrarEsquerda = !mostrarEsquerda
                mostrarAcima = !mostrarAcima
            }
        } else if (!mostrarAcima) {
            if (mostrarEsquerda) { // inferior esquerdo
                leftMargin = currentCoordinateX.toInt() - 280
                topMargin = currentCoordinateY.toInt() + 10
                mostrarEsquerda = !mostrarEsquerda
            } else { // inferior direito
                leftMargin = currentCoordinateX.toInt() - 40
                topMargin = currentCoordinateY.toInt() - 100
                mostrarEsquerda = !mostrarEsquerda
                mostrarAcima = !mostrarAcima
            }
        }
    }

    private fun exibirDialog(lupa: Lupa) {
        val playerActivity = (context as PlayerActivity)
        playerActivity.mostrarInfo(lupa)
    }

    fun moveChapeu() {
        val imagemChapeu = rootView.findViewById<ImageView>(R.id.image_hat)
        with(imagemChapeu) {
            minimumWidth = sizeImage
            minimumHeight = sizeImage
            if (visibility == View.INVISIBLE)
                visibility = View.VISIBLE
            x = currentCoordinateX - width / 2
            y = currentCoordinateY - height / 2
        }
    }

    private fun lerCoordenadas() {
        listCoordinateX = mutableListOf()
        listCoordinateY = mutableListOf()
        val readerX = BufferedReader(InputStreamReader(context.assets.open("coordinates/coord_x_2"), "UTF-8"))
        val readerY = BufferedReader(InputStreamReader(context.assets.open("coordinates/coord_y_2"), "UTF-8"))
        var mLineX = readerX.readLine()
        while (mLineX  != null) {
            listCoordinateX.add(java.lang.Float.parseFloat(mLineX))
            mLineX = readerX.readLine()
        }
        var mLineY = readerY.readLine()
        while (mLineY  != null) {
            listCoordinateY.add(java.lang.Float.parseFloat(mLineY))
            mLineY = readerY.readLine()
        }
        readerX.close()
        readerY.close()
    }

}