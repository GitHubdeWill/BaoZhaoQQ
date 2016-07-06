package com.willhd.baozhaoqq;

import android.view.accessibility.AccessibilityNodeInfo;

/**
 * Created by William on 2016/7/6.
 */

public class UserMsg extends Msg{
    public final String TYPE = "User Msg";

    public UserMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
    }


}
