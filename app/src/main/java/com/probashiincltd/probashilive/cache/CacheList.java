package com.probashiincltd.probashilive.cache;




import com.probashiincltd.probashilive.config.ConfigManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CacheList {
    final File dir;
    final LinkedHashMap<String, ConfigManager> list;
    private String lastKey = "0";


    public CacheList(String dir, String format, String uniqueKeyMap) {
        this.dir = new File(dir);
        Map<String, ConfigManager> map = new HashMap<>();
        if (!this.dir.exists() && !this.dir.mkdirs()) throw new RuntimeException("failed to access directory");
        try (Stream<Path> walkStream = Files.walk(Paths.get(dir))) {
            walkStream
                    .filter(p -> p.toFile().isFile())
                    .filter(f -> f.toString().endsWith(format))
                    .forEach(f -> {
                        String identifier = f.getFileName().toString().replace("." + format, "");
                        if (format.equals("xml")) {
                            try {
                                ConfigManager manager = new ConfigManager();
                                manager.load(new String(Files.readAllBytes(f)), identifier);
                                map.put(identifier, manager);
                                lastKey = Integer.parseInt(identifier) > Integer.parseInt(lastKey)? identifier : lastKey;
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        list = map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public void add(String xml){
        ConfigManager manager = new ConfigManager();
        lastKey = Integer.toString(Integer.parseInt(lastKey) + 1);
        manager.load(xml, lastKey);
        list.put(lastKey, manager);
        set(lastKey, "xml", xml);
    }
    public boolean remove(String index, String format){
        if (index.equals(lastKey)) lastKey = Integer.toString(Integer.parseInt(lastKey) - 1);
        list.remove(index);
        return new File(dir.getAbsolutePath()+"/"+index+"."+format).delete();
    }
    public void replace(String index, String xml){
        ConfigManager manager = new ConfigManager();
        manager.load(xml, lastKey);
        list.replace(index, manager);
        set(index, "xml", xml);
    }
    private void set(String identifier, String format, String content) {
        File file = new File(dir.getAbsolutePath()+"/"+identifier+"."+format);
        try {
            if (!file.exists() && !file.createNewFile()) throw new RuntimeException("Cache file not created");
            FileOutputStream fos= new FileOutputStream(file, false);
            fos.write(content.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
