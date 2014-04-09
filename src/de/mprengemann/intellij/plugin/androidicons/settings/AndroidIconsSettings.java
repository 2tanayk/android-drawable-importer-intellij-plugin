package de.mprengemann.intellij.plugin.androidicons.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.TextBrowseFolderListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.http.util.TextUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

/**
 * User: marcprengemann
 * Date: 04.04.14
 * Time: 10:32
 */
public class AndroidIconsSettings implements Configurable {
  private JComponent                                        mComponent;
  private JPanel                                            mPanel;
  private com.intellij.openapi.ui.TextFieldWithBrowseButton textFieldHome;
  private JLabel                                            foundColorsText;
  private JLabel                                            foundAssetsText;

  private VirtualFile selectedFile;
  private String      persistedFile;
  private boolean selectionPerformed = false;

  @Nullable
  @Override
  public JComponent createComponent() {
    persistedFile = SettingsHelper.getAssetPathString();
    VirtualFile loadedFile = VirtualFileManager.getInstance().findFileByUrl(persistedFile);
    if (loadedFile != null) {
      textFieldHome.setText(loadedFile.getCanonicalPath());
      selectedFile = loadedFile;
    }

    FileChooserDescriptor workingDirectoryChooserDescriptor = FileChooserDescriptorFactory.createSingleFolderDescriptor();
    String title = "Select res directory";
    workingDirectoryChooserDescriptor.setTitle(title);
    textFieldHome.addBrowseFolderListener(title, null, null, workingDirectoryChooserDescriptor);
    textFieldHome.addBrowseFolderListener(new TextBrowseFolderListener(workingDirectoryChooserDescriptor) {
      @Override
      protected void onFileChoosen(@NotNull VirtualFile chosenFile) {
        super.onFileChoosen(chosenFile);
        selectionPerformed = true;
        selectedFile = chosenFile;
        scanForAssets();
      }
    });

    scanForAssets();
    mComponent = mPanel;
    return mComponent;
  }

  private void scanForAssets() {
    int colorCount = 0;
    int assetCount = 0;
    if (this.selectedFile.getCanonicalPath() != null) {
      File assetRoot = new File(this.selectedFile.getCanonicalPath());
      final FilenameFilter systemFileNameFiler = new FilenameFilter() {
        @Override
        public boolean accept(File file, String s) {
          return !s.startsWith(".");
        }
      };
      File[] colorDirs = assetRoot.listFiles(systemFileNameFiler);
      if (colorDirs != null) {
        for (File file : colorDirs) {
          if (file.isDirectory()) {
            colorCount++;
          }
        }

        if (colorDirs.length >= 1) {
          File exColorDir = colorDirs[0];
          File[] densities = exColorDir.listFiles(systemFileNameFiler);
          if (densities != null && densities.length >= 1) {
            File exDensity = densities[0];
            File[] assets = exDensity.listFiles(systemFileNameFiler);
            for (File asset : assets) {
              if (!asset.isDirectory()) {
                String extension = asset.getName().substring(asset.getName().lastIndexOf(".") + 1);
                if (extension.equalsIgnoreCase("png")) {
                  assetCount++;
                }
              }
            }
          }
        }
      }
    }
    foundColorsText.setText(colorCount + " colors");
    foundAssetsText.setText(assetCount + " drawables per color");
  }

  @Override
  public boolean isModified() {
    boolean isModified = false;

    if (selectionPerformed) {
      if (!persistedFile.equalsIgnoreCase(selectedFile.getUrl())) {
        isModified = true;
      }
    } else if (!TextUtils.isEmpty(persistedFile) && selectedFile == null) {
      isModified = true;
    }

    return isModified;
  }

  @Override
  public void apply() throws ConfigurationException {
    SettingsHelper.saveAssetPath(selectedFile);
    if (selectedFile != null) {
      persistedFile = selectedFile.getUrl();
      selectionPerformed = false;
    }
  }

  @Override
  public void reset() {
  }

  @Override
  public void disposeUIResources() {
  }

  @Nls
  @Override
  public String getDisplayName() {
    return "Android Icons";
  }

  @Nullable
  @Override
  public String getHelpTopic() {
    return null;
  }
}
