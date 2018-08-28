package com.lapic.thomas.explorador_primeira_tela.view

import android.content.Context
import android.util.AttributeSet
import android.widget.VideoView

class CustomVideoView : VideoView {

    private var mListener: PlayPauseListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {}

    fun setPlayPauseListener(listener: PlayPauseListener) {
        this.mListener = listener
    }

    override fun pause() {
        super.pause()
        mListener?.onPauseVideo()
    }

    override fun start() {
        super.start()
        mListener!!.onPlayVideo()
    }

    interface PlayPauseListener {
        fun onPlayVideo()
        fun onPauseVideo()
    }

}
