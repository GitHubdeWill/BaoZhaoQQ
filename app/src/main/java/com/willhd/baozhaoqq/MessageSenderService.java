package com.willhd.baozhaoqq;

import android.Manifest;
import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by William on 2016/5/11.
 */
public class MessageSenderService extends AccessibilityService{

    private final String TAG = "QQNotifierService";


    private AccessibilityNodeInfo rootNodeInfo;
    private Robot robot;
    private long lastCheckedTime = 0;
    private boolean shouldReload = false;

    private static boolean group_changed = true;

    @TargetApi(23)
    private boolean checkPermission() {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return true;
        }
        try {
            int hasWriteEPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (hasWriteEPermission != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "QQ Robot No Permission", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }catch (Exception e){
            Log.e(TAG, "No Permission");
            Toast.makeText(this, "QQ Robot No Permission", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    @Override
    public void onAccessibilityEvent (AccessibilityEvent event) {
        Log.e(TAG, "Service Started");
        Log.d(TAG, "Time last checked" + lastCheckedTime);
        Log.d(TAG, "Time now:" + System.currentTimeMillis());
        if (lastCheckedTime == 0) lastCheckedTime = System.currentTimeMillis();
        if (!checkPermission()) return;
        if(robot == null || shouldReload) {
            robot = new Robot();
            shouldReload = false;
        }
        this.rootNodeInfo = event.getSource();
        if (this.rootNodeInfo == null) return;

        if((event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) || (System.currentTimeMillis() != lastCheckedTime)){
            Log.e(TAG, "Ready to fetch");
            if (getRootInActiveWindow() != null) recycle(getRootInActiveWindow());
            lastCheckedTime = System.currentTimeMillis();
        }


    }

    public void resetRobot() {
        shouldReload = true;
    }


    public void recycle(AccessibilityNodeInfo info) {
        if (info == null) return;
        if (info.getChildCount() == 0) {
            Log.d(TAG, "child:" + info.getClassName());
            //Log.d(TAG, "Dialog:" + info.canOpenPopup());
            Log.d(TAG, "Text：" + info.getText());
            Log.d(TAG, "windowId:" + info.getWindowId());
            Log.d(TAG, "ViewId:" + info.getViewIdResourceName());

            robot.onReceive(info, this);

        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    @Override
    public void onInterrupt () {

    }
}
