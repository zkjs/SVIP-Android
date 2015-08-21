
package com.zkjinshi.base.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * 判断应用是否root工具类
 * 开发者：JimmyZhang
 * 日期：2015/7/24
 * Copyright (C) 2015 深圳中科金石科技有限公司
 * 版权所有
 */
public class RootUtils {

    /**
     * 获取root状态
     * @return
     */
    public static boolean isRoot() {
        boolean isRoot = false;
        String sys = System.getenv("PATH");
        ArrayList<String> commands = new ArrayList<String>();
        String[] path = sys.split(":");
        for (int i = 0; i < path.length; i++) {
            String commod = "ls -l " + path[i] + "/su";
            commands.add(commod);
        }
        ArrayList<String> res = run("/system/bin/sh", commands);
        String response = "";
        for (int i = 0; i < res.size(); i++) {
            response += res.get(i);
        }
        String root = "-rwsr-sr-x root     root";
        if (response.contains(root)) {
            isRoot = true;
        }

        return isRoot;
    }
    
    private static ArrayList<String> run(String shell, ArrayList<String> commands) {
        ArrayList<String> output = new ArrayList<String>();
        Process process =null;
        try {
             process = Runtime.getRuntime().exec(shell);

            BufferedOutputStream shellInput = new BufferedOutputStream(
                    process.getOutputStream());
            BufferedReader shellOutput = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            for (String command : commands) {
                shellInput.write((command + " 2>&1\n").getBytes());
            }

            shellInput.write("exit\n".getBytes());
            shellInput.flush();

            String line;
            while ((line = shellOutput.readLine()) != null) {
                output.add(line);
            }

            process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally{
            process.destroy();
        }

        return output;
    }
}
