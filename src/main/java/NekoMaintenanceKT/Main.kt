package NekoMaintenanceKT

import NekoMaintenanceKT.files.ConfigFile
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        plugin = this
        Companion.config = ConfigFile(this, "messages")
        Bukkit.getConsoleSender().sendMessage(commands.c("""
    &f[Neko&5Maintenance&f] &aStarted successfully.
    &f╔╗╔╔═╗╦╔═╔═╗&5╔╦╗╔═╗╦╔╗╔╔╦╗╔═╗╔╗╔╔═╗╔╗╔╔═╗╔═╗
    &f║║║║╣ ╠╩╗║ ║&5║║║╠═╣║║║║ ║ ║╣ ║║║╠═╣║║║║  ║╣ 
    &f╝╚╝╚═╝╩ ╩╚═╝&5╩ ╩╩ ╩╩╝╚╝ ╩ ╚═╝╝╚╝╩ ╩╝╚╝╚═╝╚═╝
    
    &2v1.3.2 by LukeTheNeko
    &2https://github.com/LukeTheNeko/NekoMaintenance
    
    """.trimIndent()))
        getCommand("maintenance").executor = commands()
    }

    companion object {
        @JvmField
        var plugin: Main? = null
        @JvmField
        var config: ConfigFile? = null
        @JvmStatic
        fun c(msg: String?): String {
            return ChatColor.translateAlternateColorCodes('&', msg)
        }
    }
}