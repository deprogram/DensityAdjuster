package com.oumugai.densityadjuster.Utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by deprogram on 10/8/2014.
 */
public class Files {
    private static final String TAG = "Files";

    public static void copyFile(String source, String destination) throws IOException {
        copyFile(new File(source), new File(destination));
    }

    public static void copyFile(File source, File destination) throws IOException {
        InputStream in = new FileInputStream(source);
        OutputStream out = new FileOutputStream(destination);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }


    public static String convertFileToString (String filePath) throws IOException {
        return convertFileToString(new File(filePath));
    }

    public static String convertFileToString(File input) throws IOException {
        FileInputStream inputStream = new FileInputStream(input);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            builder.append(line).append("\n");
        }
        reader.close();
        inputStream.close();
        return builder.toString();
    }

    public static void writeStringToFile(String fileContents, String output) throws IOException {
        writeStringToFile(fileContents, new File(output));
    }

    public static void writeStringToFile(String fileContents, File output) throws IOException {
        FileOutputStream outputStream = new FileOutputStream(output, false);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
        writer.write(fileContents);
        writer.close();
        outputStream.close();
    }

    public static List<String> convertFileToStrings(String path) {
        List<String> strings = new ArrayList<String>(0);
        try {
            FileInputStream inputStream = new FileInputStream(new File(path));
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                strings.add(line);
            }
            reader.close();
            inputStream.close();
        } catch (IOException e) {
            Log.e(TAG, "convertFileToStrings() threw converting path " + path + ": " + e, e);
        }
        return strings;
    }
}
