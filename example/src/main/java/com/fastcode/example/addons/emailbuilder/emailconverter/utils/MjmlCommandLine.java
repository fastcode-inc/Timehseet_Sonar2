package com.fastcode.example.addons.emailbuilder.emailconverter.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

//@Service
public class MjmlCommandLine {

    private MjmlCommandLine(){}

    public static String executeCommand(String mjmlTemplate) {
        try {
            String formedCommand = getCommand(mjmlTemplate);
            Process p = Runtime.getRuntime().exec(formedCommand);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String outPutHtml = reader.lines().reduce("", (a, b) -> a + b);
            return outPutHtml;
        } catch (Exception e) {
            return "";
        }
    }

    private static String getCommand(String mjmlTemplate) {
        String command = "mjml " + mjmlTemplate;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        if (isWindows) {
            return "cmd.exe /c" + command;
        } else {
            return "sh -c" + command;
        }
    }
}
