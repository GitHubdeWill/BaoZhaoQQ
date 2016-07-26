package com.willhd.baozhaoqq;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by William on 2016/7/6.
 */

public class OtherMsg extends Msg{
    private final String TAG = "Robot";
    public final String TYPE = "Other Msg";



    public OtherMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
    }

    public void outputResponse () {
        Log.d(TAG, "OtherMsg output");
    }
}
