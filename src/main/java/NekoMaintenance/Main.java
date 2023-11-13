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
                " __  _ ___ _  ____  __ __  __  _ __  _ _____ ___ __  _  __  __  _  ______  \n" +
                "|  \\| | __| |/ /__\\|  V  |/  \\| |  \\| |_   _| __|  \\| |/  \\|  \\| |/ _/ __| \n" +
                "| | ' | _||   < \\/ | \\_/ | /\\ | | | ' | | | | _|| | ' | /\\ | | ' | \\_| _|  \n" +
                "|_|\\__|___|_|\\_\\__/|_| |_|_||_|_|_|\\__| |_| |___|_|\\__|_||_|_|\\__|\\__/___| \n" +
                ""));

        plugin = this;
        getCommand("maintenance").setExecutor(new commands());
    }
}