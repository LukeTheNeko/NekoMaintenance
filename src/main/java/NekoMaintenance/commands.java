package NekoMaintenance

import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.ListenerPriority
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketContainer
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.WrappedServerPing
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerLoginEvent
import java.io.File
import java.io.IOException
import java.util.ArrayList

class Commands : CommandExecutor, Listener {
    private var manutencaoAtivada = false

    init {
        Main.plugin?.server?.pluginManager?.registerEvents(this, Main.plugin)
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        val p: Player = sender
        if (!p.hasPermission("NekoMaintenance.commands")) {
            p.sendMessage(c(Main.msgF.config?.getString("messages.no-permission") ?: ""))
            return true
        }

        if (args.isEmpty()) {
            p.sendMessage(c(Main.msgF.config?.getString("messages.maintenance-on-off") ?: ""))
            return true
        }

        if (args[0].equals("on", ignoreCase = true) || args[0].equals("ligar", ignoreCase = true) || args[0].equals("ativar", ignoreCase = true)) {
            if (manutencaoAtivada) {
                p.sendMessage(c(Main.msgF.config?.getString("messages.already-enabled") ?: ""))
                return true
            }

            manutencaoAtivada = true
            inject()
            saveMessagesToFile()

            kickPlayersWithoutPermission()

            p.sendMessage(c(Main.msgF.config?.getString("messages.maintenance-enabled") ?: ""))
            return true
        } else if (args[0].equals("off", ignoreCase = true) || args[0].equals("desligar", ignoreCase = true) || args[0].equals("desativar", ignoreCase = true)) {
            if (!manutencaoAtivada) {
                p.sendMessage(c(Main.msgF.config?.getString("messages.already-disabled") ?: ""))
                return true
            }

            manutencaoAtivada = false
            p.sendMessage(c(Main.msgF.config?.getString("messages.maintenance-disabled") ?: ""))
            return true
        }

        p.sendMessage(c(Main.msgF.config?.getString("messages.maintenance-on-off") ?: ""))
        return true
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
        val file = File(Main.plugin?.dataFolder, "messages.yml")
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
        val motd: String = Main.msgF.config?.getString("motd") ?: ""
        val versionProtocol: Int = Main.msgF.config?.getInt("versionProtocol") ?: 0
        val versionName: String = Main.msgF.config?.getString("versionName") ?: ""

        val container: PacketContainer = event.packet
        val ping: WrappedServerPing = container.serverPings.read(0)
        ping.players = ArrayList()
        ping.motD = c(motd)
        ping.versionProtocol = versionProtocol
        ping.versionName = c(versionName)
    }

    companion object {
        fun c(msg: String?): String {
            return ChatColor.translateAlternateColorCodes('&', msg ?: "")
        }
    }

    @EventHandler
    fun onPlayerLogin(event: PlayerLoginEvent) {
        if (manutencaoAtivada && !event.player.hasPermission("NekoMaintenance.join")) {
            val kickMessage: String = Main.msgF.config?.getString("messages.maintenance-kick") ?: ""
            event.disallow(PlayerLoginEvent.Result.KICK_OTHER, c(kickMessage))
        }
    }

    fun kickPlayersWithoutPermission() {
        for (player: Player in Main.plugin?.server?.onlinePlayers ?: emptyList()) {
            if (!player.hasPermission("NekoMaintenance.join")) {
                val kickMessage: String = Main.msgF.config?.getString("messages.maintenance-kick") ?: ""
                player.kickPlayer(c(kickMessage))
            }
        }
    }
}