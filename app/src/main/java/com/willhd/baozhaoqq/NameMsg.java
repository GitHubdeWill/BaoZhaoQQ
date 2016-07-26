package com.willhd.baozhaoqq;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.regex.Pattern;

/**
 * Created by William on 2016/7/6.
 */

public class NameMsg extends Msg{
    private final String TAG = "Robot";
    public final String TYPE = "Name Msg";

    private final String QQ_NAMEFORMAT = "[（\\(][^\\(^（^\\)）]{1,64}[）\\)][^\\(^\\)]{1,64}:";

    public NameMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
        Log.d(TAG, "added to Name"+nodeInfo.getText().toString());
    }

    private void checkName (AccessibilityNodeInfo info){
        String name = info.getText().toString();
        String raw;
        Log.e(TAG, "Name discovered: "+name);
        if (!Pattern.compile(QQ_NAMEFORMAT).matcher(name).matches()) {
            raw = "@" + name.replace(":", " ")
                    + "\n少侠马甲格式不对哦!\n请修改群马甲（工种）昵称。";
            //robot.addToOutput(raw);
        }
    }

    public void outputResponse () {
        Log.d(TAG, "Msg output");
        //check format
        checkName(info);
    }


}
