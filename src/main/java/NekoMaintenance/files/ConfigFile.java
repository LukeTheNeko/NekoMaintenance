package NekoMaintenance.files;

import NekoMaintenance.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;

public class ConfigFile {
    private final Main plugin;
    private final String fyml;
    private FileConfiguration dataConfig = null;
    private File configFile = null;

    public ConfigFile(Main plugin, String fileName) {
        this.plugin = plugin;
        fyml = fileName + ".yml";
        saveDC();
    }

    public void reloadConfig() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), fyml);
        dataConfig = YamlConfiguration.loadConfiguration(configFile);

        InputStream defaultStream = plugin.getResource(fyml);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            dataConfig.setDefaults(defaultConfig);
        }
    }
    public FileConfiguration getConfig() {
        if (dataConfig == null) {
            reloadConfig();
        }
        return dataConfig;
    }
    public void deletePath(String path) {
        dataConfig.set(path, null);
        saveConfig();
    }
    public void saveConfig() {
        if (dataConfig == null || configFile == null)
            return;
        try {
            getConfig().save(configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Unable to save file" + configFile, e);
        }
    }
    public void saveDC() {
        if (configFile == null)
            configFile = new File(plugin.getDataFolder(), fyml);

        if (!configFile.exists()) {
            plugin.saveResource(fyml, false);
        }
    }
}