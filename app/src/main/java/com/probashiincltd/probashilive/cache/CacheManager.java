package com.probashiincltd.probashilive.cache;


import static com.probashiincltd.probashilive.functions.Functions.getUnsafeOkHttpClient;

import android.graphics.BitmapFactory;
import android.util.Log;
import android.webkit.URLUtil;


import com.probashiincltd.probashilive.callbacks.ImageLoadCallback;
import com.probashiincltd.probashilive.config.ConfigManager;
import com.probashiincltd.probashilive.config.Item;
import com.probashiincltd.probashilive.config.Unit;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Stream;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;



public class CacheManager {
    static File cacheDir;
    static ConfigManager configManager = new ConfigManager();
    static HashMap<String, CacheList> cacheList = new HashMap<>();

    public interface ThreadCallback {
        void onReceive(InputStream inputStream);

        void onError(Exception e);
    }

    public static void setCacheDir(String dir) {
        cacheDir = new File(dir);
        configManager = new ConfigManager();
        cacheList = new HashMap<>();
        Log.e("DirectoryCreated", "called");
        if (!cacheDir.exists() && !cacheDir.mkdirs())
            throw new RuntimeException("failed to access directory");
    }

    public static void load(String subDir, String format) {
        try (Stream<Path> walkStream = Files.walk(Paths.get(cacheDir + subDir))) {
            walkStream
                    .filter(p -> p.toFile().isFile())
                    .filter(f -> f.toString().endsWith(format))
                    .forEach(f -> {
                        String identifier = f.getFileName().toString().replace("." + format, "");
                        if (format.equals("xml")) {
                            try {
                                configManager.load(new String(Files.readAllBytes(f)), identifier);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class Instance {
        final String identifier;
        final String format;
        final File file;
        com.probashiincltd.probashilive.config.Callback callback = new com.probashiincltd.probashilive.config.Callback();

        public Instance(String identifier, String format) {
            this.identifier = identifier;
            this.format = format;
            file = new File(cacheDir.getAbsolutePath() + "/" + identifier + "." + format);
        }

        public void setCallback(com.probashiincltd.probashilive.config.Callback callback) {
            this.callback = callback;
            configManager.get(identifier).setCallback(this.callback);
        }

        public void replace(String keyMap, String value) {
            configManager.get(identifier + "/" + keyMap).change(value);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void replace(String keyMap, Node value) {
            configManager.get(identifier + "/" + keyMap).change(value);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void add(String keyMap, String value) {
            String[] keys = keyMap.split("/");
            String[] modifiedArray = Arrays.copyOfRange(keys, 0, keys.length - 1);
            configManager.get(identifier + "/" + String.join("/", modifiedArray)).add(keys[keys.length - 1], value);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void add(String keyMap, Unit value) {
            String[] keys = keyMap.split("/");
            String[] modifiedArray = Arrays.copyOfRange(keys, 0, keys.length - 1);
            configManager.get(identifier + "/" + String.join("/", modifiedArray)).add(keys[keys.length - 1], value);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void add(String keyMap, Node value) {
            String[] keys = keyMap.split("/");
            String[] modifiedArray = Arrays.copyOfRange(keys, 0, keys.length - 1);
            configManager.get(identifier + "/" + String.join("/", modifiedArray)).add(keys[keys.length - 1], value);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void remove(String keyMap) {
            String[] keys = keyMap.split("/");
            String[] modifiedArray = Arrays.copyOfRange(keys, 0, keys.length - 1);
            configManager.get(identifier + "/" + String.join("/", modifiedArray)).remove(keys[keys.length - 1]);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public void removeAttribute(String keyMap, String attribute) {
            configManager.get(identifier + "/" + keyMap).removeAttribute(attribute);
            set(identifier, format, configManager.get(identifier).toString());
        }

        public Unit get(String keyMap) {
            String delimiter = "/";
            String[] keys = keyMap.split(delimiter);
            String[] modifiedArray = Arrays.copyOfRange(keys, 1, keys.length);
            Unit unit = configManager.get(identifier).get(keys[0]);
            for (String str : modifiedArray) {
                unit = unit.get(str);
                if (unit == null) {
                    return null;
                }
            }
            return unit;
        }

        public Item get() {
            return configManager.getItem(identifier);
        }

        public void get(String keyMap, Callback callback) {
            try {
                Unit unit1 = get(keyMap);
                callback.onCacheLoad(unit1);
                Unit unit2 = callback.sourceRequest().get(keyMap);
                if (!unit1.equal(unit2)) {
                    callback.onSourceLoad(unit2);
                    this.callback.onChange();
                    set(this.identifier, format, unit2.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public long getAge() {
            long current = System.currentTimeMillis();
            long fileTime = file.lastModified();
            return current - fileTime;
        }

    }

    public static void set(String identifier, String format, String content) {
        File file = new File(cacheDir.getAbsolutePath() + "/" + identifier + "." + format);

        try {
            if (!file.exists() && !file.createNewFile())
                throw new RuntimeException("Cache file not created");
            FileOutputStream fos = new FileOutputStream(file, false);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.close();
            configManager.load(content, identifier);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void makeList(String identifier, String subDir, String format, String uniqueKeyMap) {
        cacheList.put(identifier, new CacheList(cacheDir + "/" + subDir, format, uniqueKeyMap));
    }

    public static CacheList getList(String identifier) {
        return cacheList.get(identifier);
    }

    public static void httpBinaryCache(String identifier, String url) {

    }

    static String getMd5(String input) {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            StringBuilder hashText = new StringBuilder(no.toString(16));
            while (hashText.length() < 32) {
                hashText.insert(0, "0");
            }
            return hashText.toString();
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


    public static String getLocalFile(String identifier,String url){
        String[] prts = url.split("\\.");
        return cacheDir + "/" + identifier + "/" + getMd5(url)+"."  + prts[prts.length-1];
    }

    public static void httpCache(String pos, String identifier, String url, ImageLoadCallback imageLoadCallback) throws IOException {

        if (url == null || url.equals("") || !URLUtil.isValidUrl(url)) {
            return;
        }

        File file = new File(cacheDir + "/" + identifier + "/");
        URL obj = null;
        if (!file.exists() && !file.mkdirs()) throw new RuntimeException("Cache file not created");
        String[] prts = url.split("\\.");
//        file = new File(file + getMd5(url) + ".bin");

        Log.e("UrlHttpCache",url);
        file = new File(file + getMd5(url)+"." + prts[prts.length-1]);

        InputStream targetStream;
        if (file.exists()) {
            targetStream = new FileInputStream(file);
            imageLoadCallback.onSuccess(targetStream);
        } else if (!file.createNewFile()) throw new RuntimeException("Cache file not created");
        else {
            File finalFile = file;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttpClient client = getUnsafeOkHttpClient();
                    Request request;
                    request = new Request.Builder().url(url).build();
                    client.newCall(request).enqueue(new okhttp3.Callback() {
                        @Override
                        public void onFailure(@NotNull Call call, @NotNull IOException e) {
                            imageLoadCallback.onFailed(e.toString());
                            Thread.interrupted();
                        }

                        @Override
                        public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                            InputStream bytestream = response.body().byteStream();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            try {
                                byte[] buffer = new byte[1024];
                                int len;
                                while ((len = bytestream.read(buffer)) > -1) {
                                    baos.write(buffer, 0, len);
                                }
                                baos.flush();
                            } catch (IOException e) {
                                e.fillInStackTrace();
                            }
                            InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
                            InputStream is3 = new ByteArrayInputStream(baos.toByteArray());
                            if(BitmapFactory.decodeStream(is3)!=null){
                                FileOutputStream fos = new FileOutputStream(finalFile, false);
                                int read;
                                byte[] bytes = new byte[8192];
                                while ((read = is2.read(bytes)) != -1) {
                                    fos.write(bytes, 0, read);
                                }
                                fos.close();
                            }
                            imageLoadCallback.onSuccess(is1);
                            Thread.interrupted();
                        }
                    });
                }
            }).start();

        }
    }

    public static void saveLocalImageWithUrl(String identifier, String url, InputStream inputStream, ImageLoadCallback imageLoadCallback) {
        try {


            if (url == null || url.equals("")) {
                return;
            }

            File file = new File(cacheDir + "/" + identifier + "/");
            URL obj = null;
            if (!file.exists() && !file.mkdirs())
                throw new RuntimeException("Cache file not created");
            file = new File(file + "/" + getMd5(url) + ".bin");
            InputStream targetStream;
            if (file.exists()) {
                targetStream = new FileInputStream(file);
                imageLoadCallback.onSuccess(targetStream);
                return;

            } else if (!file.createNewFile()) throw new RuntimeException("Cache file not created");
            else if (inputStream != null) {


                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) > -1) {
                        baos.write(buffer, 0, len);
                    }
                    baos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                InputStream is2 = new ByteArrayInputStream(baos.toByteArray());


                FileOutputStream fos = new FileOutputStream(file, false);
                int read;
                byte[] bytes = new byte[8192];
                while ((read = is2.read(bytes)) != -1) {
                    fos.write(bytes, 0, read);
                }
                fos.close();
                imageLoadCallback.onSuccess(is1);
                return;

            }
        } catch (Exception e) {
            imageLoadCallback.onFailed(e.toString());
            return;
        }
        imageLoadCallback.onFailed("not found");
    }
}
