package kasuga.lib.core.addons;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AddonFolderLoader {
    private final Path directory;

    public AddonFolderLoader(Path addonsDirectory){
        this.directory = addonsDirectory;
    }

    public void init(){
        if(Files.notExists(directory)){
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
