package de.mprengemann.intellij.plugin.androidicons;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.intellij.openapi.components.ApplicationComponent;
import de.mprengemann.intellij.plugin.androidicons.controllers.DefaultControllerFactory;
import de.mprengemann.intellij.plugin.androidicons.controllers.IControllerFactory;
import de.mprengemann.intellij.plugin.androidicons.model.IconPack;
import de.mprengemann.intellij.plugin.androidicons.model.Resolution;
import de.mprengemann.intellij.plugin.androidicons.resources.ResourceLoader;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class IconApplication implements ApplicationComponent {

    private IControllerFactory controllerFactory;

    @NotNull
    public String getComponentName() {
        return getClass().getName();
    }

    public void initComponent() {
        IconPack androidIcons = null;
        IconPack materialIcons = null;
        try {
            final File contentFile = ResourceLoader.getFile("content.json");
            final FileReader fileReader = new FileReader(contentFile);
            final Type listType = new TypeToken<ArrayList<IconPack>>() {}.getType();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(Resolution.class, new Resolution.Deserializer());
            final Gson gson = gsonBuilder.create();
            final List<IconPack> iconPacks = gson.fromJson(fileReader, listType);

            androidIcons = iconPacks.get(0);
            materialIcons = iconPacks.get(1);
        } catch (Exception e) {
            e.printStackTrace();
        }

        controllerFactory = new DefaultControllerFactory(androidIcons, materialIcons);
    }

    public void disposeComponent() {
        controllerFactory.tearDown();
        controllerFactory = null;
    }

    public IControllerFactory getControllerFactory() {
        return controllerFactory;
    }
}