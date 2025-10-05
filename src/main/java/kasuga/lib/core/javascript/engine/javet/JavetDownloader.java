package kasuga.lib.core.javascript.engine.javet;

import com.caoccao.javet.enums.JSRuntimeType;
import com.caoccao.javet.exceptions.JavetException;
import com.caoccao.javet.interop.loader.IJavetLibLoadingListener;
import com.caoccao.javet.interop.loader.JavetLibLoader;
import com.caoccao.javet.utils.JavetOSUtils;
import kasuga.lib.core.util.data_type.Pair;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.File;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class JavetDownloader implements IJavetLibLoadingListener {

    record Source(String sourceName, MessageFormat normalUrl){
        private String getOSArch() {
            // Copied from JaVeT's Lib Loader (JavetLibLoader.java)
            if (JavetOSUtils.IS_WINDOWS) {
                return "x86_64";
            } else if (JavetOSUtils.IS_LINUX) {
                return JavetOSUtils.IS_ARM64 ? "arm64" : "x86_64";
            } else if (JavetOSUtils.IS_MACOS) {
                return JavetOSUtils.IS_ARM64 ? "arm64" : "x86_64";
            } else {
                if (JavetOSUtils.IS_ANDROID) {
                    if (JavetOSUtils.IS_ARM) {
                        return "arm";
                    }

                    if (JavetOSUtils.IS_ARM64) {
                        return "arm64";
                    }

                    if (JavetOSUtils.IS_X86) {
                        return "x86";
                    }

                    if (JavetOSUtils.IS_X86_64) {
                        return "x86_64";
                    }
                }

                return null;
            }
        }
        private String getOSName() {
            // Copied from JaVeT's Lib Loader (JavetLibLoader.java)
            if (JavetOSUtils.IS_WINDOWS) {
                return "windows";
            } else if (JavetOSUtils.IS_LINUX) {
                return "linux";
            } else if (JavetOSUtils.IS_MACOS) {
                return "macos";
            } else {
                return JavetOSUtils.IS_ANDROID ? "android" : null;
            }
        }

        public String getUrl(JSRuntimeType runtimeType) {
            return normalUrl.format(
                new Object[]{
                    runtimeType.getName(),
                    getOSName(),
                    getOSArch(),
                    runtimeType.isI18nEnabled() ? "-i18n" : "",
                    JavetLibLoader.LIB_VERSION
                }
            );
        }
    }

    protected static List<Source> SOURCES = List.of(
      new Source("Alibaba Cloud Mirror", new MessageFormat("https://maven.aliyun.com/repository/public/com/caoccao/javet/javet-{0}-{1}-{2}{3}/{4}/javet-{0}-{1}-{2}{3}-{4}.jar")),
      new Source("Maven Repository", new MessageFormat("https://repo1.maven.org/maven2/com/caoccao/javet/com/caoccao/javet/javet-{0}-{1}-{2}{3}/{4}/javet-{0}-{1}-{2}{3}-{4}.jar"))
    );

    public static File tryDownload(JSRuntimeType runtimeType) {
        JavetLibLoader loader = new JavetLibLoader(runtimeType);
        File v8Path = new File(FMLLoader.getGamePath().toFile().getParentFile(), "native-libraries/javet");

        if(!v8Path.exists()){
            v8Path.mkdirs();
        }

        String fileName = "";

        if(JavetOSUtils.IS_ANDROID || JavetOSUtils.OS_NAME.contains("Android")) {
            throw new RuntimeException("Invalid OS/Architecture for JaVeT, please using the GraalJS mode instead.");
        }

        try{
            fileName = loader.getLibFileName();
        }catch (JavetException exception) {
            throw new RuntimeException("Invalid OS/Architecture for JaVeT, please using the GraalJS mode instead.", exception);
        }

        File v8File = new File(v8Path, fileName);

        if(v8File.exists())
            return v8Path;

        download(v8File, fileName, loader);

        return v8Path;
    }

    private static File download(File v8File, String fileName, JavetLibLoader loader) {
        // Download using
        for(Source source : SOURCES) {
            String url = source.getUrl(loader.getJSRuntimeType());
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpGet httpGet = new HttpGet(url);
                try (var response = httpClient.execute(httpGet)) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try (var inputStream = response.getEntity().getContent();
                             ZipInputStream zipInputStream = new ZipInputStream(inputStream)
                        ) {
                            String name = zipInputStream.getNextEntry().getName();
                            System.out.println("File: {}".formatted(name));
                            if(name != null && name.contains(fileName)) {
                                FileOutputStream outputStream = new FileOutputStream(v8File);
                                zipInputStream.transferTo(outputStream);
                                outputStream.close();
                                return v8File;
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to download from " + source.sourceName + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to download the V8 library. Please check your network connection or try again later. Or you can download the libjavet-xxx-xxxx from mavens and extract to [version dir]/native-libraries/javet/");
    }

    @Override
    public File getLibPath(JSRuntimeType jsRuntimeType) {
        return tryDownload(jsRuntimeType);
    }

    @Override
    public boolean isDeploy(JSRuntimeType jsRuntimeType) {
        return false;
    }
}
