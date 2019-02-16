package io.zirui.localStorageTest;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class CsvStorage {
    private static final String NEW_LINE_SEPARATOR = "\n";
    private static final String TAG_WRITE_CSV = "Write CSV";
    private static final String TAG_DIRECTORY = "Create directory";

    private static final String FILE_HEADER = "value1,value2,value3";

    public static void writeCsvFile(String filename, String albumName, List<String> list) {
        if (list == null || list.isEmpty()) return;

        File myDir = getPublicAlbumStorageDir(albumName);
        File file = new File(myDir, filename);

        boolean hasHeader = file.exists();

        FileWriter writer = null;

        try {
            writer = new FileWriter(file, true);

            if (!hasHeader) {
                writer.append(FILE_HEADER);
                writer.append(NEW_LINE_SEPARATOR);
            }

            for (String s : list) {
                writer.append(s);
                writer.append(NEW_LINE_SEPARATOR);
            }
            Log.i(TAG_WRITE_CSV, "Success");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.flush();
                writer.close();
            } catch (IOException e) {
                Log.e(TAG_WRITE_CSV, "Failed");
                e.printStackTrace();
            }
        }
    }

    public static void readCsvFile(String filename) {

    }

    private static File getPublicAlbumStorageDir(String albumName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG_DIRECTORY, "Directory not created");
            }
        }
        return file;
    }
}
