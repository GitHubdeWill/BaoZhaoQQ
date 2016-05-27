package com.willhd.baozhaoqq;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.regex.Pattern;

/**
 * Created by william on 5/26/16.
 */
public class Robot{
    private final String TAG = "Robot";
    private final String QQ_BZ = "爆照!";
    private final String QQ_NAME_PAT = "[^`]{1,255}:";
    private final String QQ_ADD_RESP = "#add [^:]{1,255}::[^:]{1,255}";
    private final static String QQ_SEND = "发送";

    private List<String> names = new ArrayList<>();

    private boolean shouldWrite = false;
    private boolean shouldSend = false;
    private boolean pending = false;
    private boolean busy = false;
    private boolean shown = false;
    private String lastKey = "";

    private List<String> group_names;
    private HashMap<String, String> responses;
    private Queue<String> record;

    public Robot () {
        group_names = new ArrayList<>();
        responses = new HashMap<>();
        record = new ArrayBlockingQueue<>(30);


        Log.e(TAG, "Start creating");
        String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/QQrobot";
        File f = new File(local_file);
        if(!f.exists()||!f.isDirectory()){
            f.mkdirs();
        }

        Log.e(TAG, "Start creating 0");
        File f0 = new File(f.getAbsolutePath(), "/groups.txt");
        if(f0.exists()) {
            try {
                FileInputStream in_g = new FileInputStream(f0);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                String temp;
                temp = reader.readLine();
                while ((temp != null) && (!temp.equals(""))) {
                    group_names.add(temp);
                    temp = reader.readLine();
                }
                Log.d(TAG, "File inputted");
            } catch (Exception e1) {
                Log.e(TAG, "input Error");
                e1.printStackTrace();
            }
        } else {
            try {
                Log.e(TAG, f0.getPath());
                if (!f0.createNewFile()) {
                    System.out.println("File already exists");
                } else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                Log.e(TAG, "create Error");
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 1");
        File f1 = new File(f.getAbsolutePath(), "/responses.txt");
        if(f1.exists()) {
            try {
                FileInputStream in_r = new FileInputStream(f1);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_r));
                String[] temp;
                temp = reader.readLine().split("::");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    responses.put(temp[0], temp[1]);
                    temp = reader.readLine().split("::");
                }
                Log.d(TAG, "File inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f1.createNewFile()) {
                    System.out.println("File already exists");
                }else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void onReceive (AccessibilityNodeInfo info, MessageSenderService s){

        if (info.getClassName().toString().equals("android.widget.TextView") && !record.contains(info.getText().toString())){
            record.add(info.getText().toString());
            Log.d(TAG, "Queue Added "+info.getText().toString());
            if (record.size() > 18){
                Log.d(TAG, "Queue Removed "+record.poll());
            }
        }
        //Match name and copy
        /*if (info.getText() != null && Pattern.compile(QQ_NAME_PAT).matcher(info.getText().toString()).matches()) {
            String name = info.getText().toString().replace(":", "");
            Log.e(TAG, "Name discovered: "+name);
            if (!names.contains(name) && !pending){
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager)s.getSystemService(s.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("name", "@"+name+" "+QQ_BZ);
                mClipboard.setPrimaryClip(mClip);
                Log.e(TAG, "Copied "+name);
                pending = true;
                shouldWrite = true;
                names.add(name);
                busy = false;
            }
        }*/

        //send response
        if (info.getText() != null && responses.containsKey(info.getText().toString()) && !info.getText().toString().equals(lastKey)) {
            String raw = responses.get(info.getText().toString());
            Log.e(TAG, "Key discovered: "+raw);
            if (!pending && !record.contains(raw)){
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager)s.getSystemService(s.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("resp", raw);
                mClipboard.setPrimaryClip(mClip);
                lastKey = info.getText().toString();
                Log.e(TAG, "Copied "+raw);
                shown = false;
                pending = true;
                shouldWrite = true;
                busy = false;
            }
        }

        //show commands
        if (info.getText() != null && info.getText().toString().equals("~show") && !shown) {
            String raw = responses.toString();
            if (!pending && !record.contains(raw)){
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager)s.getSystemService(s.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("list", raw);
                mClipboard.setPrimaryClip(mClip);
                lastKey = info.getText().toString();
                shown = true;
                Log.e(TAG, "Copied "+raw);
                pending = true;
                shouldWrite = true;
                busy = false;
            }
        }

        //Add response
        if (info.getText() != null && Pattern.compile(QQ_ADD_RESP).matcher(info.getText().toString()).matches()) {
            String raw = info.getText().toString().replace("#add ", "");
            Log.e(TAG, "Adder discovered: "+raw);
            if (!responses.containsKey(raw.split("::")[0]) && !pending){
                busy = true;
                ClipboardManager mClipboard;
                mClipboard = (ClipboardManager)s.getSystemService(s.CLIPBOARD_SERVICE);
                ClipData mClip;
                mClip = ClipData.newPlainText("Adder", "Command\n\n"+raw.split("::")[0]+"\n\n"+raw.split("::")[1]+"\n\nAdded!");
                mClipboard.setPrimaryClip(mClip);
                Log.e(TAG, "Copied "+raw);
                shown = false;
                pending = true;
                shouldWrite = true;
                responses.put(raw.split("::")[0],raw.split("::")[1]);
                String local_file = Environment.getExternalStorageDirectory().getAbsolutePath()+"/QQrobot/responses.txt";
                try {
                    File file = new File(local_file);
                    FileOutputStream fos = new FileOutputStream(file, true);
                    String ww = raw+"\n";
                    fos.write(ww.getBytes());
                    fos.close();
                    Log.d(TAG, "Added to responses.txt");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                busy = false;
            }
        }

        //Paste from Clipboard
        if (info.getClassName().toString().equals("android.widget.EditText") && shouldWrite && !busy) {
            busy = true;
            info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
            Log.e(TAG, "Pasted");
            shouldSend = true;
            shouldWrite = false;
            busy = false;
        }
        //Click send
        if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals(QQ_SEND) && shouldSend && !busy) {
            busy = true;
            info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            Log.e(TAG, "Sent");
            shouldSend = false;
            pending = false;
            busy = false;
        }

    }
}
