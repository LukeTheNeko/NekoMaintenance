package NekoMaintenanceKT

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.io.File
import java.io.IOException

class commands : CommandExecutor, Listener {
    private var manutencaoAtivada = false

    init {
        Main.plugin!!.server.pluginManager.registerEvents(this, Main.plugin)
        manutencaoAtivada = loadMaintenanceState()
        if (manutencaoAtivada) {
            inject()
            kickPlayersWithoutPermission()
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val p = sender
        if (!p.hasPermission("NekoMaintenance.commands")) {
            p.sendMessage(c(Main.config!!.getConfig().getString("messages.no-permission")))
            return true
        }
        if (args.size == 0) {
            p.sendMessage(c(Main.config!!.getConfig().getString("messages.maintenance-on-off")))
            return true
        }
        if (args[0].equals("on", ignoreCase = true) || args[0].equals("ligar", ignoreCase = true) || args[0].equals("ativar", ignoreCase = true)) {
            if (manutencaoAtivada) {
                p.sendMessage(c(Main.config!!.getConfig().getString("messages.already-enabled")))
                return true
            }
            manutencaoAtivada = true
            saveMaintenanceState(true)
            inject()
            saveMessagesToFile()
            kickPlayersWithoutPermission()
            p.sendMessage(c(Main.config!!.getConfig().getString("messages.maintenance-enabled")))
            return true
        } else if (args[0].equals("off", ignoreCase = true) || args[0].equals("desligar", ignoreCase = true) || args[0].equals("desativar", ignoreCase = true)) {
            if (!manutencaoAtivada) {
                p.sendMessage(c(Main.config!!.getConfig().getString("messages.already-disabled")))
                return true
            }
            manutencaoAtivada = false
            saveMaintenanceState(false)
            p.sendMessage(c(Main.config!!.getConfig().getString("messages.maintenance-disabled")))
            return true
        }
        p.sendMessage(c(Main.config!!.getConfig().getString("messages.maintenance-on-off")))
        return true
    }

    fun saveMaintenanceState(state: Boolean) {
        val file = File(Main.plugin!!.dataFolder, "config.yml")
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config.set("maintenance", state)
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadMaintenanceState(): Boolean {
        val file = File(Main.plugin!!.dataFolder, "config.yml")
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        return config.getBoolean("maintenance", false)
    }

    fun inject() {
        ProtocolLibrary.getProtocolManager()
                .addPacketListener(object : PacketAdapter(Main.plugin, ListenerPriority.NORMAL, PacketType.Status.Server.SERVER_INFO) {
                    override fun onPacketSending(event: PacketEvent) {
                        if (!manutencaoAtivada) {
                            return
                        }
                        loadMessagesFromFile(event)
                    }
                })
    }

    fun saveMessagesToFile() {
        val file = File(Main.plugin!!.dataFolder, "messages.yml")
        val config: FileConfiguration = YamlConfiguration.loadConfiguration(file)
        config.options().header("Configuracoes para mensagens")
        config.options().copyHeader(true)
        config.options().copyDefaults(true)
        try {
            config.save(file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun loadMessagesFromFile(event: PacketEvent) {
        val motd = Main.config!!.getConfig().getString("motd")
        val versionProtocol = Main.config!!.getConfig().getInt("versionProtocol")
        val versionName = Main.config!!.getConfig().getString("versionName")
        val container = event.packet
        val ping = container.serverPings.read(0)
        ping.setPlayers(ArrayList())
        ping.setMotD(c(motd))
        ping.versionProtocol = versionProtocol
        ping.versionName = c(versionName)
    }

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        if (manutencaoAtivada && !event.player.hasPermission("NekoMaintenance.join")) {
            val kickMessage = Main.config!!.getConfig().getString("messages.maintenance-kick")
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, c(kickMessage))
        }
    }

    fun kickPlayersWithoutPermission() {
        for (player in Main.plugin!!.server.onlinePlayers) {
            if (!player.hasPermission("NekoMaintenance.join")) {
                val kickMessage = Main.config!!.getConfig().getString("messages.maintenance-kick")
                player.kickPlayer(c(kickMessage))
            }
        }
    }

    companion object {
        fun c(msg: String?): String {
            return ChatColor.translateAlternateColorCodes('&', msg)
        }
    }
}
