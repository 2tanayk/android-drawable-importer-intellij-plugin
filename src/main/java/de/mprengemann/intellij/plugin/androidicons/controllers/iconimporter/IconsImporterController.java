package de.mprengemann.intellij.plugin.androidicons.controllers.iconimporter;

import com.intellij.openapi.project.Project;
import com.jgoodies.common.base.Objects;
import de.mprengemann.intellij.plugin.androidicons.controllers.defaults.IDefaultsController;
import de.mprengemann.intellij.plugin.androidicons.controllers.icons.IIconPackController;
import de.mprengemann.intellij.plugin.androidicons.controllers.icons.androidicons.IAndroidIconsController;
import de.mprengemann.intellij.plugin.androidicons.controllers.icons.materialicons.IMaterialIconsController;
import de.mprengemann.intellij.plugin.androidicons.images.RefactoringTask;
import de.mprengemann.intellij.plugin.androidicons.model.Format;
import de.mprengemann.intellij.plugin.androidicons.model.ImageAsset;
import de.mprengemann.intellij.plugin.androidicons.model.ImageInformation;
import de.mprengemann.intellij.plugin.androidicons.model.Resolution;
import de.mprengemann.intellij.plugin.androidicons.util.RefactorUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IconsImporterController implements IIconsImporterController {

    private final Set<IconsImporterObserver> observerSet;
    private final IAndroidIconsController androidIconsController;
    private final IMaterialIconsController materialIconsController;

    private Set<Resolution> exportResolutions;
    private ImageAsset selectedAsset;
    private String selectedSize;
    private String selectedColor;
    private Format format;
    private String exportName;
    private String exportRoot;

    public IconsImporterController(IDefaultsController defaultsController,
                                   IAndroidIconsController androidIconsController,
                                   IMaterialIconsController materialIconsController) {
        this.androidIconsController = androidIconsController;
        this.materialIconsController = materialIconsController;
        this.observerSet = new HashSet<IconsImporterObserver>();

        final ImageAsset defaultImageAsset = defaultsController.getImageAsset();
        if (defaultImageAsset == null) {
            final String category = materialIconsController.getCategories().get(0);
            setSelectedAsset(materialIconsController.getAssets(category).get(0));
        } else {
            this.selectedSize = defaultsController.getSize();
            this.selectedColor = defaultsController.getColor();
            setSelectedAsset(defaultImageAsset);
        }

        this.format = Format.PNG;
        this.exportName = null;
        this.exportResolutions = defaultsController.getResolutions();
    }

    public IconsImporterController(IDefaultsController defaultsController,
                                   IMaterialIconsController materialIconsController) {
        this.androidIconsController = null;
        this.materialIconsController = materialIconsController;
        this.observerSet = new HashSet<IconsImporterObserver>();

        final ImageAsset defaultImageAsset = defaultsController.getImageAsset();
        if (defaultImageAsset == null ||
            !defaultImageAsset.getIconPack().equals(materialIconsController.getIconPack().getId())) {
            final String category = materialIconsController.getCategories().get(0);
            setSelectedAsset(materialIconsController.getAssets(category).get(0));
        } else {
            setSelectedAsset(defaultImageAsset);
        }

        this.exportName = null;
        this.format = Format.XML;
        this.exportResolutions = Collections.singleton(Resolution.ANYDPI);
    }

    @Override
    public void addObserver(IconsImporterObserver observer) {
        observerSet.add(observer);
        notifyUpdated();
    }

    @Override
    public void removeObserver(IconsImporterObserver observer) {
        observerSet.remove(observer);
    }

    @Override
    public void tearDown() {
        observerSet.clear();
    }

    @Override
    public void setExportRoot(String exportRoot) {
        if (exportRoot.equals(this.exportRoot)) {
            return;
        }
        this.exportRoot = exportRoot;
        notifyUpdated();
    }

    @Override
    public void setSelectedIconPack(String iconPack) {
        if (selectedAsset.getIconPack().equals(iconPack)) {
            return;
        }
        final IIconPackController controller = getControllerForIconPackId(iconPack);
        final String category = controller.getCategories().get(0);
        selectedAsset = controller.getAssets(category).get(0);
        updateColorAndSize();
        notifyUpdated();
    }

    @NotNull
    private IIconPackController getControllerForIconPackId(String iconPack) {
        IIconPackController controller;
        if (materialIconsController.getId().equals(iconPack)) {
            controller = materialIconsController;
        } else if (androidIconsController != null &&
                   androidIconsController.getId().equals(iconPack)) {
            controller = androidIconsController;
        } else {
            throw new IllegalArgumentException(iconPack + " not found!");
        }
        return controller;
    }

    @Override
    public void setSelectedCategory(String category) {
        if (selectedAsset.getCategory().equals(category)) {
            return;
        }
        selectedAsset = materialIconsController.getAssets(category).get(0);
        updateColorAndSize();
        notifyUpdated();
    }

    @Override
    public void setSelectedAsset(ImageAsset asset) {
        selectedAsset = asset;
        updateColorAndSize();
        notifyUpdated();
    }

    private void updateColorAndSize() {
        if (!selectedAsset.getColors().contains(selectedColor)) {
            selectedColor = selectedAsset.getColors().get(0);
        }
        if (!selectedAsset.getSizes().contains(selectedSize)) {
            selectedSize = selectedAsset.getSizes().get(0);
        }
    }

    @Override
    public void setSelectedSize(String size) {
        if (selectedSize.equals(size) &&
            selectedAsset.getSizes().contains(size)) {
            return;
        }
        selectedSize = size;
        if (!selectedAsset.getSizes().contains(selectedSize)) {
            selectedSize = selectedAsset.getSizes().get(0);
        }
        notifyUpdated();
    }

    @Override
    public void setSelectedColor(String color) {
        if (selectedColor.equals(color) &&
            selectedAsset.getColors().contains(color)) {
            return;
        }
        selectedColor = color;
        if (!selectedAsset.getColors().contains(selectedColor)) {
            selectedSize = selectedAsset.getColors().get(0);
        }
        notifyUpdated();
    }

    @Override
    public void setExportName(String exportName) {
        if (exportName.equals(selectedAsset.getName())) {
            exportName = null;
        }
        if (Objects.equals(exportName, this.exportName)) {
            return;
        }
        this.exportName = exportName;
        notifyUpdated();
    }

    @Override
    public String getExportName() {
        return exportName != null ? exportName : selectedAsset.getName();
    }

    @Override
    public String getExportRoot() {
        return exportRoot;
    }

    @Override
    public File getImageFile(ImageAsset asset, String color, Resolution resolution) {
        return getSelectedIconPack().getImageFile(asset, color, resolution);
    }

    @Override
    public File getThumbnailFile(ImageAsset asset) {
        final IIconPackController iconPackController = getControllerForIconPackId(asset.getIconPack());
        return iconPackController.getImageFile(asset, "black", iconPackController.getThumbnailResolution());
    }

    @Override
    public File getSelectedImageFile() {
        return getSelectedImageFile(Resolution.HDPI);
    }

    @Override
    public File getSelectedImageFile(Resolution resolution) {
        final IIconPackController iconPackController = getSelectedIconPack();
        return iconPackController.getImageFile(selectedAsset, selectedColor, selectedSize, resolution);
    }

    @Override
    public List<String> getCategories() {
        return getSelectedIconPack().getCategories();
    }

    @Override
    public List<ImageAsset> getAssets() {
        return getSelectedIconPack().getAssets(selectedAsset.getCategory());
    }

    @Override
    public List<String> getSizes() {
        return selectedAsset.getSizes();
    }

    @Override
    public List<String> getColors() {
        return selectedAsset.getColors();
    }

    @Override
    public ImageAsset getSelectedAsset() {
        return selectedAsset;
    }

    @Override
    public String getSelectedSize() {
        return selectedSize;
    }

    @Override
    public String getSelectedColor() {
        return selectedColor;
    }

    @Override
    public IIconPackController getSelectedIconPack() {
        return getControllerForIconPackId(selectedAsset.getIconPack());
    }

    @Override
    public String getSelectedCategory() {
        return selectedAsset.getCategory();
    }

    @Override
    public void setFormat(Format format) {
        if (this.format == format) {
            return;
        }
        this.format = isNinePatch() ? Format.PNG : format;
        notifyUpdated();
    }

    @Override
    public boolean isNinePatch() {
        return false;
    }

    @Override
    public Format getFormat() {
        return format;
    }

    @Override
    public RefactoringTask getTask(Project project) {
        final RefactoringTask task = new RefactoringTask(project);
        final ImageInformation baseInformation = ImageInformation.newBuilder()
                                                                 .setExportName(getExportName())
                                                                 .setExportPath(getExportRoot())
                                                                 .setFormat(getFormat())
                                                                 .build();
        for (Resolution resolution : exportResolutions) {
            ImageInformation.Builder imageInformationBuilder = ImageInformation.newBuilder(baseInformation);
            imageInformationBuilder.setTargetResolution(resolution);
            imageInformationBuilder.setVector(resolution == Resolution.ANYDPI);
            final File selectedImageFile;
            if (getSelectedAsset().getResolutions().contains(resolution)) {
                selectedImageFile = getSelectedImageFile(resolution);
            } else {
                if (getSelectedIconPack().getId().equals(materialIconsController.getIconPack().getId())) {
                    selectedImageFile = getSelectedImageFile(Resolution.HDPI);
                    imageInformationBuilder.setFactor(RefactorUtils.getScaleFactor(resolution, Resolution.HDPI));
                } else if (androidIconsController != null &&
                           getSelectedIconPack().getId().equals(androidIconsController.getIconPack().getId())) {
                    selectedImageFile = getSelectedImageFile(Resolution.XXHDPI);
                    imageInformationBuilder.setFactor(RefactorUtils.getScaleFactor(resolution, Resolution.XXHDPI));
                } else {
                    throw new IllegalStateException();
                }
            }
            imageInformationBuilder.setImageFile(selectedImageFile);
            task.addImage(imageInformationBuilder.build());
        }
        return task;
    }

    @Override
    public void setExportResolution(Resolution resolution, boolean export) {
        if (exportResolutions.contains(resolution) && !export) {
            exportResolutions.remove(resolution);
        } else if (!exportResolutions.contains(resolution) && export) {
            exportResolutions.add(resolution);
        }
    }

    @Override
    public Set<Resolution> getExportResolutions() {
        return exportResolutions;
    }

    private void notifyUpdated() {
        for (IconsImporterObserver observer : observerSet) {
            observer.updated();
        }
    }
}
