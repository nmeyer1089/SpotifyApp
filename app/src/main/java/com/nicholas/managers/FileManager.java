package com.nicholas.managers;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Nicholas on 9/11/2017.
 */

public class FileManager {

    public static Context context;

    public static boolean writeFile(String filename, String toWrite) {
        File file = new File(context.getFilesDir(), filename);

        try {
            file.createNewFile();
            if (!file.canWrite()) {
                return false;
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write(toWrite);
            writer.close();
        } catch (IOException e) {
            Log.d("File Manager", "Write IO Exception" + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    public static String readFile(String filename) {
        String read = "";
        File file = new File(context.getFilesDir(), filename);
        if (!file.canRead()) {
            return "";
        }
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            read = reader.readLine();
        } catch (IOException e) {
            Log.d("File Manager", "Read IO Exception" + e.getLocalizedMessage());
        }
        return read;
    }
}
