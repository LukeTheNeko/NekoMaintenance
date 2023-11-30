package NekoMaintenance;

import NekoMaintenance.commands;
import NekoMaintenance.files.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import static NekoMaintenance.commands.c;
public final class Main extends JavaPlugin {

    public static Main plugin;
    public static ConfigFile msgF;

    @Override
    public void onEnable() {
        msgF = new ConfigFile(this, "messages");
        Bukkit.getConsoleSender().sendMessage(c("&f[Neko&5Maintenance&f] &aInicializado com sucesso." +
                "\n" +
                "&f╔╗╔╔═╗╦╔═╔═╗&5╔╦╗╔═╗╦╔╗╔╔╦╗╔═╗╔╗╔╔═╗╔╗╔╔═╗╔═╗\n" +
                "&f║║║║╣ ╠╩╗║ ║&5║║║╠═╣║║║║ ║ ║╣ ║║║╠═╣║║║║  ║╣ \n" +
                "&f╝╚╝╚═╝╩ ╩╚═╝&5╩ ╩╩ ╩╩╝╚╝ ╩ ╚═╝╝╚╝╩ ╩╝╚╝╚═╝╚═╝\n" +
                "\n" +
                "&2v1.0.2 by LukeTheNeko\n" +
                "&2https://github.com/LukeTheNeko/NekoMaintenance\n\n"));

        plugin = this;
        getCommand("maintenance").setExecutor(new commands());
    }
}