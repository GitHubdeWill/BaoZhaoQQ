package com.willhd.baozhaoqq;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by William on 2016/7/6.
 */

public class Msg {
    private final String TAG = "Msg";

    protected AccessibilityNodeInfo info;
    protected Robot robot;
    protected String infoID;
    protected String resp;
    public final String TYPE = "General";

    public Msg (AccessibilityNodeInfo nodeInfo, Robot robot){
        this.info = nodeInfo;
        this.robot = robot;
        this.infoID = getInfoID();
    }

    protected String getInfoID () {
        return info.toString().split(";")[0].split("@")[1];
    }
    protected String getText () {
        return info.getText().toString();
    }

    public void outputResponse () {
        Log.d(TAG, "Msg output");
    }
}
