package kasuga.lib.core.client.model.anim_instance;

import com.google.common.collect.Maps;
import kasuga.lib.KasugaLib;
import kasuga.lib.KasugaLibConfig;
import kasuga.lib.core.annos.Beta;
import kasuga.lib.core.client.model.anim_json.Animation;
import kasuga.lib.core.client.model.anim_model.AnimModel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

@Beta
@OnlyIn(Dist.CLIENT)
public class AnimCacheManager {
    public static final String PATH = "kasuga/anim_cache/";
    // public static final String MAPPING_FILE = PATH + "mappings.nbt";
    public static final AnimCacheManager INSTANCE = new AnimCacheManager();
    private final HashMap<String, byte[]> cache;
    public AnimCacheManager() {
        this.cache = Maps.newHashMap();
        File file = new File(PATH);
        if (!file.isDirectory() && !file.mkdirs()) {
            throw new RuntimeException("Falied to create kasuga lib cache file " + PATH);
        }
    }

    public boolean save(AnimationInstance instance) throws IOException {
        if (!systemEnabled()) return true;
        File file = new File(PATH + getPureFileName(instance)
                .replace("/", "{%20}")
                .replace(":", "{%05}"));
        if (!file.exists() && !file.createNewFile()) return false;
        boolean flag = instance.writeToFile(file);
        if (!flag) return false;
        try (FileInputStream fis = new FileInputStream(file)) {
            cache.put(getPureFileName(instance), fis.readAllBytes());
            return true;
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to open cache file: " + getPureFileName(instance));
            return false;
        }
    }

    public boolean load(String pureFileName) {
        if (!systemEnabled()) return true;
        File file = new File(PATH + pureFileName
                .replace("/", "{%20}")
                .replace(":", "{%05}"));
        if (!file.isFile()) return false;
        try(FileInputStream fis = new FileInputStream(file)) {
            byte[] b = fis.readAllBytes();
            cache.put(pureFileName, b);
            return true;
        } catch (IOException e) {
            KasugaLib.MAIN_LOGGER.error("Failed to open cache file: " + pureFileName);
            return false;
        }
    }

    public void scanFolder() {
        if (!systemEnabled()) return;
        File file = new File(PATH);
        if (!file.isDirectory()) return;
        int counter = 0;
        for (File f : Objects.requireNonNull(file.listFiles())) {
            if (!f.isFile()) continue;
            if (!f.getName().endsWith(".anim_cache")) continue;
            if (load(f.getName().replace("{%05}", ":").replace("{%20}", "/"))) counter++;
        }
        KasugaLib.MAIN_LOGGER.info("Successfully loaded " + counter + " cache files.");
    }

    public static String getPureFileName(AnimationInstance instance) {
        return instance.animation.file.location + ":" + instance.animation.name + "-" +
                instance.model.geometry.getModel().modelLocation + ":" + instance.model.geometry.getDescription().getIdentifier()
                + "-" + instance.frameRate + ".anim_cache";
    }

    public static String getPureFileName(Animation instance, AnimModel model, int frameRate) {
        return instance.file.location + ":" + instance.name + "-" +
                model.geometry.getModel().modelLocation + ":" + model.geometry.getDescription().getIdentifier()
                + "-" + frameRate + ".anim_cache";
    }

    public boolean hasCache(String key) {
        return cache.containsKey(key);
    }

    public boolean hasCache(AnimationInstance instance) {
        return hasCache(getPureFileName(instance));
    }

    public boolean hasCache(Animation animation, AnimModel model, int frameRate) {
        return hasCache(getPureFileName(animation, model, frameRate));
    }

    public byte[] getCache(String key) {
        return cache.getOrDefault(key, new byte[0]);
    }

    public byte[] getCache(Animation animation, AnimModel model, int frameRate) {
        return cache.getOrDefault(getPureFileName(animation, model, frameRate), new byte[0]);
    }

    public static boolean systemEnabled() {
        return KasugaLibConfig.CONFIG.getBoolValue("enable_animation_cache");
    }

    public void clearFolder() {
        File file = new File(PATH);
        if (!file.isDirectory()) return;
        for (File f : Objects.requireNonNull(file.listFiles())) {
            f.deleteOnExit();
        }
    }

    public void clearCaches() {
        this.cache.clear();
    }

    public void clearCachesAndFolder() {
        clearCaches();
        clearFolder();
    }
}
