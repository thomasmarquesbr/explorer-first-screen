package com.lapic.thomas.explorador_primeira_tela

import android.view.Gravity
import android.R.attr.gravity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.MediaController


internal class CustomMediaController(context: Context?,
                                     useFastForward: Boolean) : MediaController(context, useFastForward) {

    private var isFullScreen = true

    override fun setAnchorView(view: View) {
        super.setAnchorView(view)
        val closeButton = ImageView(context)
        closeButton.tag = "fullscreen"
        closeButton.setImageResource(R.drawable.ic_exit_fullscreen)
        val padding = 8
        closeButton.setPadding(padding, padding, padding, padding)
        val params = FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.END
        addView(closeButton, params)
        closeButton.setOnClickListener {
            if (isFullScreen) {
                (context as PlayerActivity).redimensionaVideo()
                closeButton.setImageResource(R.drawable.ic_fullscreen)
            } else {
                (context as PlayerActivity).maximizaVideo()
                closeButton.setImageResource(R.drawable.ic_exit_fullscreen)
            }
            isFullScreen = !isFullScreen
        }
    }
}