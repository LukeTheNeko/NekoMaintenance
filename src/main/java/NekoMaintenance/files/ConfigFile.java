package NekoMaintenance.files

import NekoMaintenance.Main
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level

class ConfigFile(private val plugin: Main, fileName: String) {
    private val fyml: String
    private var dataConfig: FileConfiguration? = null
    private var configFile: File? = null

    init {
        fyml = "$fileName.yml"
        saveDC()
    }

    fun reloadConfig() {
        if (configFile == null) configFile = File(plugin.dataFolder, fyml)
        dataConfig = YamlConfiguration.loadConfiguration(configFile)
        val defaultStream = plugin.getResource(fyml)
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            dataConfig.setDefaults(defaultConfig)
        }
    }

    val config: FileConfiguration?
        get() {
            if (dataConfig == null) {
                reloadConfig()
            }
            return dataConfig
        }

    fun deletePath(path: String?) {
        dataConfig!![path] = null
        saveConfig()
    }

    fun saveConfig() {
        if (dataConfig == null || configFile == null) return
        try {
            config!!.save(configFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Unable to save file$configFile", e)
        }
    }

    fun saveDC() {
        if (configFile == null) configFile = File(plugin.dataFolder, fyml)
        if (!configFile!!.exists()) {
            plugin.saveResource(fyml, false)
        }
    }
}