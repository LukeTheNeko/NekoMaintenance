package NekoMaintenanceKT.files
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.logging.Level

class ConfigFile(private val plugin: JavaPlugin, fileName: String) {
    private val fyml: String = "$fileName.yml"
    private var dataConfig: FileConfiguration? = null
    private var configFile: File? = null

    init {
        saveDC()
    }

    fun reloadConfig() {
        if (configFile == null) {
            configFile = File(plugin.dataFolder, fyml)
        }
        dataConfig = YamlConfiguration.loadConfiguration(configFile)

        val defaultStream = plugin.getResource(fyml)
        if (defaultStream != null) {
            val defaultConfig = YamlConfiguration.loadConfiguration(InputStreamReader(defaultStream))
            dataConfig!!.setDefaults(defaultConfig)
        }
    }

    fun getConfig(): FileConfiguration {
        if (dataConfig == null) {
            reloadConfig()
        }
        return dataConfig!!
    }

    fun deletePath(path: String) {
        dataConfig?.set(path, null)
        saveConfig()
    }

    fun saveConfig() {
        if (dataConfig == null || configFile == null) {
            return
        }
        try {
            getConfig().save(configFile)
        } catch (e: IOException) {
            plugin.logger.log(Level.SEVERE, "Não foi possível salvar o arquivo $configFile", e)
        }
    }

    fun saveDC() {
        if (configFile == null) {
            configFile = File(plugin.dataFolder, fyml)
        }

        if (!configFile!!.exists()) {
            plugin.saveResource(fyml, false)
        }
    }
}