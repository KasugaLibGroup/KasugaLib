package kasuga.lib.core.util;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevelopEnvFileContainer {


    private static Map<String, InputStream> listFiles(String path) throws IOException {
        File file = new File("../src/generated/resources/" + path);
        if (file.exists() && file.isDirectory()) {
            ArrayList<File> needToScan = new ArrayList<>(List.of(file.listFiles()));
            Map<String, InputStream> streamMap = new HashMap<>();
            while (!needToScan.isEmpty()) {
                File f = needToScan.get(0);
                if (!f.exists()) continue;
                if (!file.canRead()) continue;
                needToScan.remove(f);
                if(f.isDirectory()) {
                    File[] files = f.listFiles();
                    if(files != null)
                        needToScan.addAll(List.of(files));
                    continue;
                }
                FileInputStream fis = new FileInputStream(f);
                ByteArrayInputStream bis = new ByteArrayInputStream(fis.readAllBytes());
                fis.close();
                streamMap.put(f.getName(), bis);
            }
            return streamMap;
        } else if (file.exists() && file.isFile()) {
            Map<String, InputStream> streamMap = new HashMap<>();
            FileInputStream fis = new FileInputStream(file);
            ByteArrayInputStream bis = new ByteArrayInputStream(fis.readAllBytes());
            fis.close();
            streamMap.put(file.getName(), bis);
            return streamMap;
        } else {
            file.mkdir();
        }
        return new HashMap<>();
    }
}
