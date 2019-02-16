# Android Local (external) Storage Test

In this case, I used a cache list to avoid too much IO operation. Please modify code to make sure the last piece of new content will be writen into the file.

## Steps

### 1. Request external storage permissions

Beginning with Android 4.4 (API level 19), reading or writing files in your app's private external storage directory—accessed using `getExternalFilesDir()`—does not require the `READ_EXTERNAL_STORAGE` or `WRITE_EXTERNAL_STORAGE` permissions. So if your app supports Android 4.3 (API level 18) and lower, and you want to access only the private external storage directory, you should declare that the permission be requested only on the lower versions of Android by adding the `maxSdkVersion` attribute:

```xml
<manifest ...>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
                     android:maxSdkVersion="18" />
    ...
</manifest>
```

### 2. Ask user for permission

```java
// Customized code for class to identify which permission.
private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

@Override
protected void onCreate(Bundle savedInstanceState) {
    /** Above code.*/

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (checkAndRequestPermissions()) {
            storeToDocument(content);
        }
    } else {
        storeToDocument(content);
    }
}

// Use a list to store permissions for future potential implementations.
@RequiresApi(api = Build.VERSION_CODES.M)
private boolean checkAndRequestPermissions() {
    List<String> listPermissionsNeeded = new ArrayList<>();
    if (ContextCompat.checkSelfPermission(this, permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
        listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }
    if (!listPermissionsNeeded.isEmpty()) {
        ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
        return false;
    }
    return true;
}

// Callback function after user accept the permission.
@RequiresApi(api = Build.VERSION_CODES.M)
@Override
public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
    switch (requestCode) {
        case REQUEST_ID_MULTIPLE_PERMISSIONS:{
            Map<String, Integer> perms = new HashMap<>();
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            if (grantResults.length > 0) {
                for (int i = 0; i < permissions.length; i++) {
                    perms.put(permissions[i], grantResults[i]);
                }
                if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    storeToDocument(content);
                }else{
                    checkAndRequestPermissions();
                }
            }
        }
    }
}
```

### 3. Create a folder

```java
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
```

### 4. Write file

Make sure set `true` in `writer = new FileWriter(file, true);`, so we can always add new line to exisiting file.

```java
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
```

## Ref

- [Write/Read CSV Files in Java Example](https://examples.javacodegeeks.com/core-java/writeread-csv-files-in-java-example/)
- [Save files on device storage](https://developer.android.com/training/data-storage/files#java)
- [Requesting and allowing WRITE_EXTERNAL_STORAGE permission at runtime has no effects on the current session](https://stackoverflow.com/questions/32947638/requesting-and-allowing-write-external-storage-permission-at-runtime-has-no-effe)

## Licence

MIT