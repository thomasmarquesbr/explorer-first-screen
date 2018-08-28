package com.lapic.thomas.explorador_primeira_tela.network;

import android.util.Log;

import com.lapic.thomas.explorador_primeira_tela.PlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MulticastGroup extends MulticastManager {

    private static String tag = "first_screen";
    private static String multicastIp = "230.192.0.10";
    private static int multicastPort = 1027;

    private PlayerActivity mActivity;

    public MulticastGroup(PlayerActivity activity) {
        super(activity, tag, multicastIp, multicastPort);
        this.mActivity = activity;
    }

    @Override
    protected Runnable getIncomingMessageAnalyseRunnable() {

        String tag = incomingMessage.getTag();
        String message = incomingMessage.getMessage();

        try {
            JSONObject jsonObject = new JSONObject(URLDecoder.decode(message, "UTF-8"));
            Log.e("first", tag + " > " + message);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
