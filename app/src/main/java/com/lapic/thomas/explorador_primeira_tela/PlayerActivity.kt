package com.lapic.thomas.explorador_primeira_tela

import android.animation.LayoutTransition
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.SeekBar
import com.example.thomas.explorador_segunda_tela.model.Lupa
import com.lapic.thomas.explorador_primeira_tela.network.MulticastGroup
import com.lapic.thomas.explorador_primeira_tela.sync.Sincronizador
import com.lapic.thomas.explorador_primeira_tela.view.CustomVideoView
import kotlinx.android.synthetic.main.activity_player.*
import org.json.JSONObject
import java.net.URLEncoder

class PlayerActivity : AppCompatActivity(),
        MediaPlayer.OnPreparedListener, CustomVideoView.PlayPauseListener, MediaPlayer.OnCompletionListener {

    private val heightDevice by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }

    private val widthDevice by lazy {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
    private val width by lazy { resources.displayMetrics.widthPixels + 144 }
    private val height by lazy { resources.displayMetrics.heightPixels }
    private val mediaController by lazy { CustomMediaController(this, false) }
    private val multicastGroup by lazy { MulticastGroup(this) }
    private val controladorDesenho by lazy { ControladorDesenho(this, canvas_view, rl_canvas, widthDevice/2, heightDevice) }
    private lateinit var sincronizador: Sincronizador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        hideSystemUI()
        configuraVideo()
        Log.e("HEIGHT", "$heightDevice")
        Log.e("WIDTH", "$widthDevice")
//        object : CountDownTimer(5000, 5000) {
//            override fun onTick(millisUntilFinished: Long) {}
//            override fun onFinish() {
//                this@PlayerActivity.redimensionaVideo()
//            }
//        }.start()
//
//        val timer2 = object : CountDownTimer(30000, 30000) {
//            override fun onTick(millisUntilFinished: Long) {}
//            override fun onFinish() {
//                this@PlayerActivity.maximizaVideo()
//            }
//        }.start()
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        salvaEstadoAtualDaReproducaoDoVideo(savedInstanceState)
    }

    public override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        restaurarEstadoAtualDaReproducaoDoVideo(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        multicastGroup.startMessageReceiver()
    }

    private fun salvaEstadoAtualDaReproducaoDoVideo(savedInstanceState: Bundle) {
        savedInstanceState.putInt("CurrentPosition", video_view.currentPosition)
        video_view.pause()
    }

    private fun restaurarEstadoAtualDaReproducaoDoVideo(savedInstanceState: Bundle) {
        val tempoAtual = savedInstanceState.getInt("CurrentPosition")
        video_view.seekTo(tempoAtual)
    }

    // Interface Methods

    override fun onCompletion(mp: MediaPlayer?) {
        sincronizador.isRuning = false
        notificaSegundaTelaParaFinalizarDesenho()
    }

    override fun onPlayVideo() {
        val tempoAtualDoVideo = video_view.currentPosition/1000
        val duracaoTotal = video_view.duration/1000
        if (tempoAtualDoVideo == 0 && duracaoTotal != 0) {
            notificarSegundaTelaParaIniciarDesenho(duracaoTotal)
        } else if (tempoAtualDoVideo != 0) {
            notificaSegundaTelaParaContinuarDesenho(tempoAtualDoVideo)
        }
        Log.e("PLAYER_ACTIVITY","$tempoAtualDoVideo $duracaoTotal")
        inicializaSincronizador(
                tempoAtualDoVideo, duracaoTotal)
    }

    override fun onPauseVideo() {
        sincronizador.isRuning = false
        notificaSeguntaTelaParaPausarDesenho()
    }

    override fun onPrepared(mp: MediaPlayer?) {
        desabilitaSeekBar()
    }

    // Functions

    fun maximizaVideo() {
//        scroll_view.visibility = View.GONE
        rl_details.visibility = View.GONE
        val layoutParamsRLVideo = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        with(rl_video) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            layoutParams = layoutParamsRLVideo
            gravity = Gravity.CENTER
        }
    }

    fun redimensionaVideo() {
        val relativeLayoutParamDetails = RelativeLayout.LayoutParams(width / 2, height / 2)
        with(relativeLayoutParamDetails) {
            addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.ALIGN_RIGHT)
        }
        with(rl_details) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            layoutParams = relativeLayoutParamDetails
            visibility = View.VISIBLE
        }

        val layoutParamsRLVideo = RelativeLayout.LayoutParams(width / 2, height / 2)
        with(layoutParamsRLVideo) {
            addRule(RelativeLayout.ALIGN_PARENT_TOP)
            addRule(RelativeLayout.ALIGN_PARENT_END)
            addRule(RelativeLayout.ALIGN_RIGHT)
        }
        with(rl_video) {
            layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
            layoutParams = layoutParamsRLVideo
            gravity = Gravity.CENTER
        }



//        with(scroll_view) {
//            layoutParams = RelativeLayout.LayoutParams(width / 2, ViewGroup.LayoutParams.WRAP_CONTENT)
//            visibility = View.VISIBLE
//        }
    }

    private fun notificarSegundaTelaParaIniciarDesenho(duracaoTotal: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("START", "drawing")
        jsonObject.put("duration", duracaoTotal)
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
        multicastGroup.sendMessage(false, message)
    }

    private fun desabilitaSeekBar() {
        val idMediaController = resources.getIdentifier("mediacontroller_progress", "id", "android")
        val seekBar = mediaController.findViewById<SeekBar>(idMediaController)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {}
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                seekBar.isEnabled = false
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                seekBar.isEnabled = false
            }
        })
    }

    private fun inicializaSincronizador(tempoAtualDoVideo: Int, tempoTotalDoVideo: Int) {
        sincronizador = Sincronizador(multicastGroup, tempoAtualDoVideo, tempoTotalDoVideo)
        sincronizador.isRuning = true
    }

    private fun notificaSegundaTelaParaContinuarDesenho(tempoAtualDoVideo: Int) {
        val jsonObject = JSONObject()
        jsonObject.put("RESUME", "drawing")
        jsonObject.put("currentTime", tempoAtualDoVideo)
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    private fun configuraVideo() {
        val videoPath = "android.resource://" + packageName + "/" + R.raw.video
        with(mediaController) {
            setAnchorView(video_view)
            setMediaPlayer(video_view)
        }
        with(video_view) {
            setOnPreparedListener(this@PlayerActivity)
            setPlayPauseListener(this@PlayerActivity)
            setOnCompletionListener(this@PlayerActivity)
            setMediaController(mediaController)
            setVideoURI(Uri.parse(videoPath))
        }
        video_view.start()
    }

    private fun notificaSegundaTelaParaFinalizarDesenho() {
        val jsonObject = JSONObject()
        jsonObject.put("STOP", "drawing")
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
    }

    private fun notificaSeguntaTelaParaPausarDesenho() {
        val jsonObject = JSONObject()
        jsonObject.put("PAUSE", "drawing")
        jsonObject.put("currentTime", video_view.currentPosition/1000)
        val message = URLEncoder.encode(jsonObject.toString(), "UTF-8")
        multicastGroup.sendMessage(false, message)
    }

    fun inicializarDesenho(duration: Int) {
        controladorDesenho.inicializarDesenho(duration)
    }

    fun retomarDesenho(currentDuration: Int) {
        controladorDesenho.retomarDesenho(video_view.duration/1000, currentDuration)
    }

    fun pausarDesenho(currentDuration: Int) {
        controladorDesenho.pausarDesenho(currentDuration)
    }

    fun finalizarDesenho() {
        controladorDesenho.finalizarDesenho()
    }

    fun exibirLupa(id: Int, corLupa: String) {
        controladorDesenho.exibirLupa(id, corLupa, false)
    }

    fun mostrarInfo(lupa: Lupa) {
        tv_title.text = lupa.title
        tv_description.text = lupa.description
        iv_image.setImageResource(lupa.resIdImage)
    }


}
