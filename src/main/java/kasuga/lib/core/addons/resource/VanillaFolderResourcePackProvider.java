package kasuga.lib.core.addons.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VanillaFolderResourcePackProvider implements ResourceProvider,HierarchicalFilesystem {
    private final File file;

    public VanillaFolderResourcePackProvider(File file) {
        this.file = file;
    }

    public File getChildren(String path){
        return new File(file,"script/" + ResourceProvider.firstSplash(path));
    }
    @Override
    public InputStream open(String path) throws IOException {
        return new FileInputStream(getChildren(path));
    }

    @Override
    public boolean exists(String path) {
        return getChildren(path).exists();
    }


    @Override
    public List<String> list(String path) {
        String[] fileList = getChildren(path).list();
        if(fileList != null){
            return List.of(fileList);
        }
        return List.of();
    }
}
