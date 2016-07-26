package com.willhd.baozhaoqq;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.File;
import java.io.FileOutputStream;
import java.util.regex.Pattern;

/**
 * Created by William on 2016/7/6.
 */

public class NotifiMsg extends Msg{
    private final String TAG = "Robot";
    public final String TYPE = "Notification Msg";

    private final String QQ_ADMIN = "饭管理群-[^:]{1,64}:[^`]{1,255}";
    private final String QQ_ADD_RESP = "#add [^:]{1,255}::[^:]{1,255}";
    private final String QQ_TEACH = "说[^:]{1,255}";
    private final String QQ_SETTIME = "定时[0-9]{1,64}";
    private final String QQ_BLACKLIST = "黑名单[^:]{1,255}";
    private final String QQ_RBLACKLIST = "移除黑名单[^:]{1,255}";
    private final String QQ_JUMP = "跳去[^:]{1,64}";
    private final String QQ_GROUP = "-[^:]{1,64}:[^`]{1,255}";

    private static String pendingQroup = "";

    public NotifiMsg (AccessibilityNodeInfo nodeInfo, Robot robot){
        super(nodeInfo, robot);
        Log.d(TAG, "added to notification"+nodeInfo.getText().toString());
    }

    /*private void shuoAdmin (String cont, int l){
        String raw;
        switch (l) {
            case 0:
                raw = cont.replaceFirst("说", "");
                break;
            case 1:
                robot.addToBlacklist(cont);
                raw = "已写入";
                break;
            case 2:
                raw = cont.replaceFirst("定时", "");
                Robot.TIME = Long.parseLong(raw);
                raw = cont.replaceFirst("定时", "冷场间隔设为") + "毫秒";
                break;
            case 3:
                robot.removeBlacklist(cont);
                raw = "已删除";
                break;
            case 4:
                pendingQroup = cont.replaceFirst("跳去", "");
                raw = "小饭已准备好穿梭！";
                break;
            default:
                raw = "出错";
                break;
        }
        Log.d(TAG, "saying " + raw);
        robot.addToOutput(raw);
    }
    private void addResponse (String cont) {
        String raw = cont.replace("#add ", "");
        Log.e(TAG, "Adder discovered: " + raw);
        if (!robot.getResponses().containsKey(raw.split("::")[0])) {
            robot.addToResps(raw);
            Log.e(TAG, "Processed " + raw);
        } else {
            robot.addToOutput("命令已存在");
        }
    }

    public void outputResponse () {
        Log.d(TAG, "NotifiMsg output");

        //Admin
        if (Pattern.compile(QQ_ADMIN).matcher(info.getText().toString()).matches()) {
            String comm = info.getText().toString().split(":")[1];
            if (Pattern.compile(QQ_TEACH).matcher(comm).matches()) shuoAdmin(comm, 0);
            if (Pattern.compile(QQ_BLACKLIST).matcher(comm).matches()) shuoAdmin(comm, 1);
            if (Pattern.compile(QQ_SETTIME).matcher(comm).matches()) shuoAdmin(comm, 2);
            if (Pattern.compile(QQ_RBLACKLIST).matcher(comm).matches()) shuoAdmin(comm, 3);
            if (Pattern.compile(QQ_JUMP).matcher(comm).matches()) shuoAdmin(comm, 4);

            if (Pattern.compile(QQ_ADD_RESP).matcher(comm).matches()) addResponse(comm);
        }

        if (Pattern.compile(pendingQroup+QQ_GROUP).matcher(info.getText().toString()).matches()) {
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            robot.addToOutput("小饭我穿越啦！");
        }
    }
*/
}
