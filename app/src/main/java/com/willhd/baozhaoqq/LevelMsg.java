package com.willhd.baozhaoqq;

import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by William on 2016/7/6.
 */

public class LevelMsg extends Msg{
    private final String TAG = "Robot";
    public final String TYPE = "Level Msg";

    public LevelMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
        Log.d(TAG, "added to Level"+nodeInfo.getText().toString());
    }

    public void outputResponse () {
        Log.d(TAG, "LevelMsg output");
    }

}
