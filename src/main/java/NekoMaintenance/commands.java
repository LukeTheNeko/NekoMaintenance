package NekoMaintenance;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedServerPing;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class commands implements CommandExecutor, Listener {
    private boolean manutencaoAtivada = false;

    public commands() {
        Main.plugin.getServer().getPluginManager().registerEvents(this, Main.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;

        Player p = (Player) sender;
        if (!p.hasPermission("NekoMaintenance.commands")) {
            p.sendMessage(c(Main.msgF.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(c(Main.msgF.getConfig().getString("messages.maintenance-on-off")));
            return true;
        }

        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("ligar") || args[0].equalsIgnoreCase("ativar")) {
            if (manutencaoAtivada) {
                p.sendMessage(c(Main.msgF.getConfig().getString("messages.already-enabled")));
                return true;
            }

            manutencaoAtivada = true;
            inject();
            saveMessagesToFile();

            kickPlayersWithoutPermission();

            p.sendMessage(c(Main.msgF.getConfig().getString("messages.maintenance-enabled")));
            return true;
        } else if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("desligar") || args[0].equalsIgnoreCase("desativar")) {
            if (!manutencaoAtivada) {
                p.sendMessage(c(Main.msgF.getConfig().getString("messages.already-disabled")));
                return true;
            }

            manutencaoAtivada = false;
            p.sendMessage(c(Main.msgF.getConfig().getString("messages.maintenance-disabled")));
            return true;
        }

        p.sendMessage(c(Main.msgF.getConfig().getString("messages.maintenance-on-off")));
        return true;
    }

    public void inject() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(new PacketAdapter(Main.plugin, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
                    public void onPacketSending(PacketEvent event) {
                        if (!manutencaoAtivada) {
                            return;
                        }

                        loadMessagesFromFile(event);
                    }
                });
    }

    public void saveMessagesToFile() {
        File file = new File(Main.plugin.getDataFolder(), "messages.yml");
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);

        config.options().header("Configuracoes para mensagens");
        config.options().copyHeader(true);
        config.options().copyDefaults(true);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadMessagesFromFile(PacketEvent event) {
        String motd = Main.msgF.getConfig().getString("motd");
        int versionProtocol = Main.msgF.getConfig().getInt("versionProtocol");
        String versionName = Main.msgF.getConfig().getString("versionName");

        PacketContainer container = event.getPacket();
        WrappedServerPing ping = container.getServerPings().read(0);
        ping.setPlayers(new ArrayList<>());
        ping.setMotD(c(motd));
        ping.setVersionProtocol(versionProtocol);
        ping.setVersionName(c(versionName));
    }

    public static String c(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent event) {
        if (manutencaoAtivada && !event.getPlayer().hasPermission("NekoMaintenance.join")) {
            String kickMessage = Main.msgF.getConfig().getString("messages.maintenance-kick");
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, c(kickMessage));
        }
    }

    public void kickPlayersWithoutPermission() {
        for (Player player : Main.plugin.getServer().getOnlinePlayers()) {
            if (!player.hasPermission("NekoMaintenance.join")) {
                String kickMessage = Main.msgF.getConfig().getString("messages.maintenance-kick");
                player.kickPlayer(c(kickMessage));
            }
        }
    }
}