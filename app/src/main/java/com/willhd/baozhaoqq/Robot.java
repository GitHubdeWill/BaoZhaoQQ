package com.willhd.baozhaoqq;

import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
    private final String QQ_NEWBIE = "[^`]{1,64} 已加入该群";
    private final String QQ_JIEXIN = "#接新\n[^#]{1,255}";
    private final String QQ_XUQIU = "#需求\n[^#]{1,255}";
    private final String QQ_RECALLN = "#撤回需求\n[^#]{1,255}";
    private final String QQ_RECALLJ = "#撤回接新\n[^#]{1,255}";
    private final String QQ_HELP = "#help";
    private final String QQ_HELPS = "欢迎来到嘹亮古风交流群大家庭～\n" +
            "我是机器人小饭求眼熟～\n" +
            "------需要接新请严格按照以下格式发单：\n" +
            "#接新\n" +
            "圈名==\n" +
            "工种，个人联系及其他备注\n" +
            "------需要招人请严格按照以下格式发单：\n" +
            "#需求\n" +
            "圈名==\n" +
            "工种，个人联系及其他备注\n" +
            "------需要撤回接新请按照以下格式发：\n" +
            "#撤回接新\n" +
            "圈名\n" +
            "------需要撤回需求请按照以下格式发：\n" +
            "#撤回需求\n" +
            "圈名\n" +
            "------找需求请输入：\n" +
            "~找需求\n" +
            "------找接新请输入：\n" +
            "~找接新";

    private final static String QQ_SEND = "发送";

    private List<String> names = new ArrayList<>();

    private boolean shouldWrite = false;
    private boolean shouldSend = false;
    private boolean pending = false;
    private boolean busy = false;
    private String lastKey = "";

    private List<String> group_names;
    private HashMap<String, String> responses;
    private HashMap<String, String> needs;
    private HashMap<String, String> jiexins;
    private Queue<String> record;

    public Robot () {
        group_names = new ArrayList<>();
        responses = new HashMap<>();
        needs = new HashMap<>();
        jiexins = new HashMap<>();
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
                Log.d(TAG, "File0 inputted");
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
                Log.d(TAG, "File1 inputted");
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
                Log.e(TAG, "create Error");
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 2");
        File f2 = new File(f.getAbsolutePath(), "/jiexins.txt");
        if(f2.exists()) {
            try {
                FileInputStream in_j = new FileInputStream(f2);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_j));
                String[] temp;
                temp = reader.readLine().replace("--", "\n").split("==");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    jiexins.put(temp[0], temp[1]);
                    temp = reader.readLine().replace("--", "\n").split("==");
                }
                Log.d(TAG, "File2 inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f2.createNewFile()) {
                    System.out.println("File already exists");
                }else {
                    System.out.println("File created");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        Log.e(TAG, "Start creating 3");
        File f3 = new File(f.getAbsolutePath(), "/needs.txt");
        if(f3.exists()) {
            try {
                FileInputStream in_n = new FileInputStream(f3);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in_n));
                String[] temp;
                temp = reader.readLine().replace("--", "\n").split("==");
                while ((temp[0] != null) && (!temp[0].equals(""))) {
                    needs.put(temp[0], temp[1]);
                    temp = reader.readLine().replace("--", "\n").split("==");
                }
                Log.d(TAG, "File2 inputted");
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }else{
            try {
                if(!f3.createNewFile()) {
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


        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.TextView")) {
                //Welcome
                if (Pattern.compile(QQ_NEWBIE).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString();
                    Log.e(TAG, "newer discovered: " + raw);
                    if (!record.contains(raw) && !pending) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("Adder", "欢迎" + raw.replace(" 已加入该群", "") +
                                ",请修改群马甲（工种）昵称");
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //send response
                if (responses.containsKey(info.getText().toString()) && !info.getText().toString().equals(lastKey)) {
                    String raw = responses.get(info.getText().toString());
                    Log.e(TAG, "Key discovered: " + raw);
                    if (!pending && !record.contains(raw)) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("resp", raw);
                        mClipboard.setPrimaryClip(mClip);
                        lastKey = info.getText().toString();
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //show commands
                if (info.getText().toString().equals("~show") && !record.contains("~show")) {
                    String raw = responses.toString();
                    if (!pending && !record.contains(raw)) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list", raw);
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //show help
                if (info.getText().toString().equals(QQ_HELP) && !record.contains(QQ_HELP)) {
                    String raw = QQ_HELPS;
                    if (!pending && !record.contains(raw)) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list", raw);
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //show Xuqiu
                if (info.getText().toString().equals("~找需求") && !record.contains("~找需求")) {
                    String raw = needs.toString();
                    if (!pending && !record.contains(raw)) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list", "需求名单:\n" + raw.replace(",", ",\n"));
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //show jiexin
                if (info.getText().toString().equals("~找接新") && !record.contains("~找接新")) {
                    String raw = jiexins.toString();
                    if (!pending && !record.contains(raw)) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("list", "接新名单:\n" + raw.replace(",", ",\n"));
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //Add response
                if (Pattern.compile(QQ_ADD_RESP).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString().replace("#add ", "");
                    Log.e(TAG, "Adder discovered: " + raw);
                    if (!responses.containsKey(raw.split("::")[0]) && !pending) {
                        busy = true;
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("Adder", "Command\n\n" + raw.split("::")[0] + "\n\n" + raw.split("::")[1] + "\n\nAdded!");
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        responses.put(raw.split("::")[0], raw.split("::")[1]);
                        String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/responses.txt";
                        try {
                            File file = new File(local_file);
                            FileOutputStream fos = new FileOutputStream(file, true);
                            String ww = raw + "\n";
                            fos.write(ww.getBytes());
                            fos.close();
                            Log.d(TAG, "Added to responses.txt");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        busy = false;
                    }
                }

                //Jiexin
                if (Pattern.compile(QQ_JIEXIN).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString();
                    Log.e(TAG, "Jiexin discovered: " + raw);
                    if (raw.split("==").length < 2) {
                        Log.e(TAG, "Copied Error");
                    } else {
                        if (!jiexins.containsKey(raw.split("==")[0].replace("#接新\n", "")) && !pending) {
                            busy = true;
                            ClipboardManager mClipboard;
                            mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                            ClipData mClip;
                            mClip = ClipData.newPlainText("Jiexin", raw.replace("#", "").split("==")[0] + "\nAdded");
                            mClipboard.setPrimaryClip(mClip);
                            Log.e(TAG, "Copied " + raw);
                            pending = true;
                            shouldWrite = true;
                            jiexins.put(raw.split("==")[0].replace("#接新\n", ""), raw.split("==")[1]);
                            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/jiexins.txt";
                            try {
                                File file = new File(local_file);
                                FileOutputStream fos = new FileOutputStream(file, true);
                                String ww = raw.replace("#接新\n", "").replace("\n", "--") + "\n";
                                fos.write(ww.getBytes());
                                fos.close();
                                Log.d(TAG, "Added to jiexins.txt");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            busy = false;
                        }
                    }
                }

                //Xuqiu
                if (Pattern.compile(QQ_XUQIU).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString();
                    Log.e(TAG, "Xuqiu discovered: " + raw);
                    if (raw.split("==").length < 2) {
                        Log.e(TAG, "Copied Error");
                    } else {
                        if (!needs.containsKey(raw.split("==")[0].replace("#需求\n", "")) && !pending) {
                            busy = true;
                            ClipboardManager mClipboard;
                            mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                            ClipData mClip;
                            mClip = ClipData.newPlainText("Xuqiu", raw.replace("#", "").split("==")[0] + "\nAdded");
                            mClipboard.setPrimaryClip(mClip);
                            Log.e(TAG, "Copied " + raw);
                            pending = true;
                            shouldWrite = true;
                            needs.put(raw.split("==")[0].replace("#需求\n", ""), raw.split("==")[1]);
                            String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/needs.txt";
                            try {
                                File file = new File(local_file);
                                FileOutputStream fos = new FileOutputStream(file, true);
                                String ww = raw.replace("#需求\n", "").replace("\n", "--") + "\n";
                                fos.write(ww.getBytes());
                                fos.close();
                                Log.d(TAG, "Added to needs.txt");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            busy = false;
                        }
                    }
                }

                //Recall Needs
                if (Pattern.compile(QQ_RECALLN).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString();
                    Log.e(TAG, "Recall discovered: " + raw);
                    if (needs.containsKey(raw.replace("#撤回需求\n", "")) && !pending) {
                        busy = true;
                        needs.remove(raw.replace("#撤回需求\n", ""));
                        String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/needs.txt";
                        try {
                            File file = new File(local_file);
                            File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/tempneed.txt");

                            FileInputStream in_g = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                            String temp;
                            temp = reader.readLine();
                            while ((temp != null) && (!temp.equals(""))) {
                                if (!temp.matches(raw.replace("#撤回需求\n", "") + "[^#]{1,255}")) {
                                    writer.println(temp);
                                    Log.e(TAG, "Write " + temp);
                                }
                                temp = reader.readLine();
                            }

                            boolean result = tempFile.renameTo(file);

                            writer.close();
                            reader.close();
                            Log.e(TAG, "removed in needs.txt " + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("recall", raw.replace("#撤回需求\n", "") + "需求撤回成功!");
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }

                //Recall Jiexin
                if (Pattern.compile(QQ_RECALLJ).matcher(info.getText().toString()).matches()) {
                    String raw = info.getText().toString();
                    Log.e(TAG, "Recall discovered: " + raw);
                    if (jiexins.containsKey(raw.replace("#撤回接新\n", "")) && !pending) {
                        busy = true;
                        jiexins.remove(raw.replace("#撤回接新\n", ""));
                        String local_file = Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/jiexins.txt";
                        try {
                            File file = new File(local_file);
                            File tempFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/QQrobot/tempjiexin.txt");

                            FileInputStream in_g = new FileInputStream(file);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in_g));
                            PrintWriter writer = new PrintWriter(new FileWriter(tempFile));
                            String temp;
                            temp = reader.readLine();
                            while ((temp != null) && (!temp.equals(""))) {
                                if (!temp.matches(raw.replace("#撤回接新\n", "") + "[^#]{1,255}")) {
                                    writer.println(temp);
                                    Log.e(TAG, "Write " + temp);
                                }
                                temp = reader.readLine();
                            }

                            boolean result = tempFile.renameTo(file);

                            writer.close();
                            reader.close();
                            Log.d(TAG, "removed in jiexins.txt " + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ClipboardManager mClipboard;
                        mClipboard = (ClipboardManager) s.getSystemService(s.CLIPBOARD_SERVICE);
                        ClipData mClip;
                        mClip = ClipData.newPlainText("recall", raw.replace("#撤回接新\n", "") + "接新撤回成功!");
                        mClipboard.setPrimaryClip(mClip);
                        Log.e(TAG, "Copied " + raw);
                        pending = true;
                        shouldWrite = true;
                        busy = false;
                    }
                }
            }
        }

        //Paste from Clipboard
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.EditText") && shouldWrite && !busy) {
                busy = true;
                info.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                Log.e(TAG, "Pasted");
                shouldSend = true;
                shouldWrite = false;
                busy = false;
            }
        }

        //Click send
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals(QQ_SEND) && shouldSend && !busy) {
                busy = true;
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                Log.e(TAG, "Sent");
                shouldSend = false;
                pending = false;
                busy = false;
            }
        }

        //Add records
        if (info.getClassName() != null && info.getText() != null) {
            if (info.getClassName().toString().equals("android.widget.TextView") && !record.contains(info.getText().toString())) {
                record.add(info.getText().toString());
                Log.d(TAG, "Queue Added " + info.getText().toString());
                if (record.size() > 18) {
                    Log.d(TAG, "Queue Removed " + record.poll());
                }
            }
        }
    }
}
