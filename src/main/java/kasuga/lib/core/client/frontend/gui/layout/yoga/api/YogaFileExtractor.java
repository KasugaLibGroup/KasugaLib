package kasuga.lib.core.client.frontend.gui.layout.yoga.api;

import net.minecraftforge.fml.loading.FMLLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.jar.JarFile;

public class YogaFileExtractor {
    private static final ArrayList<FileLock> locks = new ArrayList<>();
    public static String extract() throws URISyntaxException, IOException {
        if(locks.size() > 0){
            Path versionJarPath = FMLLoader.getGamePath();
            File versionJarFile = versionJarPath.toFile();
            File versionFolder = versionJarFile.getParentFile();
            File librariesFolder = new File(versionFolder, "react-native-libraries");
            return librariesFolder.getAbsolutePath();
        }
        URI path = YogaFileExtractor.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (!path.getPath().contains(".jar")) return null;
        String jarPath = path.getPath().substring(0, path.getPath().indexOf(".jar") + 4);
        JarFile jarFile = new JarFile(jarPath);

        Path gamePath = FMLLoader.getGamePath();
        File versionPathFile = gamePath.toFile();
        File librariesFolder = new File(versionPathFile, "react-native-libraries");

        if (!librariesFolder.exists()) {
            librariesFolder.mkdirs();
        }

        jarFile.stream().filter((entry) -> entry.getName().startsWith("libraries/")).forEach((entry) -> {
            try {
                File file = new File(librariesFolder, entry.getName().substring("libraries/".length()));
                if (entry.isDirectory()) {
                    file.mkdirs();
                } else {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream fos = new FileOutputStream(file);
                    FileInputStream fis = new FileInputStream(file);
                    FileChannel outputChannel = fos.getChannel();
                    FileLock lock = outputChannel.lock();
                    jarFile.getInputStream(entry).transferTo(fos);
                    lock.release();
                    outputChannel.close();
                    fos.close();
                    FileChannel channel = fis.getChannel();
                    FileLock sharedLock = channel.lock(0, Long.MAX_VALUE, true);
                    locks.add(sharedLock);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return librariesFolder.getAbsolutePath();
    }
}
