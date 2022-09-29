package glanz.me.glansheep

import glanz.me.glansheep.database.SQLite
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.io.IOException


class test : JavaPlugin() {
    val config_file: File = File("plugins//Invsave//config.yml")
    val yamlConfiguration = YamlConfiguration.loadConfiguration(config_file)

    override fun onEnable() {
        logger.info("Enabled")
        server.pluginManager.registerEvents(EventListener, this)
        if (!config_file.exists()) {
            yamlConfiguration.set("DB_PATH","Your DB Path")
        }

        try {
            yamlConfiguration.save(this.config_file)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        val db_path = yamlConfiguration.getString("DB_PATH").toString()
        if (!(checkPlayer(sender))) {
            return false
        }else
            if (cmd.name.equals("saveinv") && checkName(sender, args)) {
                val p = sender as Player
                val invname : String = args[0]
                val check_duplication: Boolean = SQLite().saveinv(p, invname, db_path)
                if (!check_duplication) {
                    p.sendMessage("§e${invname}は既に存在しています。他の名前を使用してください")
                    return false
                }
                p.sendMessage("§e${invname}にインベントリを保存しました")
                return true
            }
            if (cmd.name.equals("selectinv")) {
                val p = sender as Player
                SQLite().selectinv_invname(p, db_path)
                return true
            }
            if (cmd.name.equals("removeinv") && checkName(sender, args)) {
                val p = sender as Player
                val invname: String = args[0]
                val check_existence: Boolean = SQLite().removeinv(p, invname, db_path)
                if (!check_existence) {
                    p.sendMessage("§e${invname}はありません")
                    return false
                }
                p.sendMessage("§e${invname}のインベントリを削除しました")
                return true
            }
        return false
    }
    fun checkName(sender: CommandSender,args: Array<out String>): Boolean {
        if (args.size == 0) {
            sender.sendMessage("§e名前を設定してください")
            return false
        } else if (args.size == 1) {
            return true
        } else {
            sender.sendMessage("§e名前は1つまでです")
            return false
        }
    }
    fun checkPlayer(sender: CommandSender): Boolean {
        if (sender is Player) {
            return true
        }
        sender.sendMessage("§eプレイヤーから実行してください")
        return false
    }
}