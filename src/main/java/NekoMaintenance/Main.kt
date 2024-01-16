package NekoMaintenance

import NekoMaintenance.files.ConfigFile
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {
    override fun onEnable() {
        msgF = ConfigFile(this, "messages")
        Bukkit.getConsoleSender().sendMessage(commands.c("""
    &f[Neko&5Maintenance&f] &aInicializado com sucesso.
    &f╔╗╔╔═╗╦╔═╔═╗&5╔╦╗╔═╗╦╔╗╔╔╦╗╔═╗╔╗╔╔═╗╔╗╔╔═╗╔═╗
    &f║║║║╣ ╠╩╗║ ║&5║║║╠═╣║║║║ ║ ║╣ ║║║╠═╣║║║║  ║╣ 
    &f╝╚╝╚═╝╩ ╩╚═╝&5╩ ╩╩ ╩╩╝╚╝ ╩ ╚═╝╝╚╝╩ ╩╝╚╝╚═╝╚═╝
    
    &2v1.1.1 by LukeTheNeko
    &2https://github.com/LukeTheNeko/NekoMaintenance
    
    
    """.trimIndent()))
        plugin = this
        getCommand("maintenance").executor = commands()
    }

    companion object {
        @JvmField
        var plugin: Main? = null
        @JvmField
        var msgF: ConfigFile? = null
    }
}