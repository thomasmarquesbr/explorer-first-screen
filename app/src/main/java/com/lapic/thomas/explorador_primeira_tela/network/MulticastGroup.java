package com.lapic.thomas.explorador_primeira_tela.network;

import android.util.Log;

import com.lapic.thomas.explorador_primeira_tela.PlayerActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class MulticastGroup extends MulticastManager {

    private static final String START = "START";
    private static final String STOP = "STOP";
    private static final String RESUME = "RESUME";
    private static final String PAUSE = "PAUSE";

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
            executeAction(jsonObject);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void executeAction(JSONObject json) throws JSONException {
        if (json.has(START)) {
            String action = json.getString(START);
            if (action.equals("drawing")) {
                mActivity.inicializarDesenho(json.getInt("duration"));
            } else if (action.equals("RED") || action.equals("GREEN") || action.equals("YELLOW") || action.equals("BLUE")) {
                mActivity.exibirLupa(json.getInt("id"), action);
            }
        } else if (json.has(RESUME)) {
            String action = json.getString(RESUME);
            if (action.equals("drawing")) {
                mActivity.retomarDesenho(json.getInt("currentTime"));
            }
        } else if (json.has(STOP)) {
            String action = json.getString(STOP);
            if (action.equals("drawing")) {
                mActivity.finalizarDesenho();
            }
        } else if (json.has(PAUSE)) {
            String action = json.getString(PAUSE);
            if (action.equals("drawing")) {
                mActivity.pausarDesenho(json.getInt("currentTime"));
            }
        }
    }

}
