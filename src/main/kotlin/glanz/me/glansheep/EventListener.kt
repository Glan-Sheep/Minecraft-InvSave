package glanz.me.glansheep

import glanz.me.glansheep.database.SQLite
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import java.io.File

object EventListener: Listener {
    val config_file: File = File("plugins//InvSave//config.yml")
    val yamlConfiguration = YamlConfiguration.loadConfiguration(config_file)

    val db_path = yamlConfiguration.getString("DB_PATH").toString()
    @EventHandler
    fun ClickEvent(e: InventoryClickEvent) {
        val p = e.whoClicked as Player
        if (e.view.title =="インベントリを選択してください") {
            val get_inv_name = e.currentItem?.itemMeta?.displayName.toString()
            p.closeInventory()
            SQLite().get_inv(p, get_inv_name, db_path)
        }
    }
}