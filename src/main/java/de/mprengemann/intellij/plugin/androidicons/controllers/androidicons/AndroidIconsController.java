package de.mprengemann.intellij.plugin.androidicons.controllers.androidicons;

import com.intellij.openapi.vfs.VirtualFile;
import de.mprengemann.intellij.plugin.androidicons.model.IconPack;
import de.mprengemann.intellij.plugin.androidicons.model.ImageAsset;
import de.mprengemann.intellij.plugin.androidicons.model.Resolution;
import de.mprengemann.intellij.plugin.androidicons.resources.ResourceLoader;

import java.io.File;
import java.util.List;

public class AndroidIconsController implements IAndroidIconsController {

    private IconPack iconPack;

    public AndroidIconsController(IconPack iconPack) {
        this.iconPack = iconPack;
    }

    @Override
    public void restorePath() {

    }

    @Override
    public void savePath() {

    }

    @Override
    public void setPath(VirtualFile file) {

    }

    @Override
    public String getPath() {
        return null;
    }

    @Override
    public String getId() {
        return iconPack.getId();
    }

    @Override
    public String getIconPackName() {
        return iconPack.getName();
    }

    @Override
    public List<ImageAsset> getAssets(String category) {
        return iconPack.getAssets();
    }

    @Override
    public List<String> getCategories() {
        return iconPack.getCategories();
    }

    @Override
    public VirtualFile getRoot() {
        return null;
    }

    @Override
    public void openBrowser() {

    }

    @Override
    public File getImageFile(ImageAsset asset, String color, Resolution resolution) {
        final String localPath = String.format("%s/%s/%s.png",
                                            color,
                                            resolution.toString(),
                                            asset.getName());
        return ResourceLoader.getFile(new File(iconPack.getPath(), localPath).getPath());
    }

    @Override
    public void tearDown() {
        iconPack = null;
    }
}
