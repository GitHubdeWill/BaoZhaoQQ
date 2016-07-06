package com.willhd.baozhaoqq;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by William on 2016/7/6.
 */

public class Msg {
    private final String TAG = "Msg";

    protected AccessibilityNodeInfo info;
    protected String infoID;
    public final String TYPE = "General";

    public Msg (AccessibilityNodeInfo nodeInfo, Robot robot){

    }
    protected String getInfoID () {
        return info.toString().split(";")[0].split("@")[1];
    }
}
