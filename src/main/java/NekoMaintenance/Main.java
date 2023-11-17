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
        Bukkit.getConsoleSender().sendMessage(c("\n&6 \n" +

                "&2Plugin Working!\n"+
                "&2v1.0.0 by LukeTheNeko\n"+
                "&2https://github.com/LukeTheNeko/NekoMaintenance\n"+
                "&5"+
                "\n" +
                "\n" +
                "  _  _ ___ _  _____    __  __   _   ___ _  _ _____ ___ _  _   _   _  _  ___ ___ \n" +
                " | \\| | __| |/ / _ \\  |  \\/  | /_\\ |_ _| \\| |_   _| __| \\| | /_\\ | \\| |/ __| __|\n" +
                " | .` | _|| ' < (_) | | |\\/| |/ _ \\ | || .` | | | | _|| .` |/ _ \\| .` | (__| _| \n" +
                " |_|\\_|___|_|\\_\\___/  |_|  |_/_/ \\_\\___|_|\\_| |_| |___|_|\\_/_/ \\_\\_|\\_|\\___|___|\n" +
                "                                                                                \n"));

        plugin = this;
        getCommand("maintenance").setExecutor(new commands());
    }
}