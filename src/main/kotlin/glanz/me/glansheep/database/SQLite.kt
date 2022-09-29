package glanz.me.glansheep.database

import glanz.me.glansheep.database.tables.InvData
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.*


class SQLite {
    fun saveinv(p: Player, invname_cmd: String, dburi: String): Boolean {
        var item_data : String = ""
        for (i in 0..35) {
            val item: ItemStack? = p.inventory.getItem(i)
            val io = ByteArrayOutputStream()
            val os = BukkitObjectOutputStream(io)
            os.writeObject(item)
            os.flush()

            val serializedObject = io.toByteArray()
            val encodeObject = String(Base64.getEncoder().encode(serializedObject))

            if (i == 0) item_data += encodeObject
            else item_data += "," + encodeObject
        }

        val uuid = p.uniqueId.toString()
        Database.connect("jdbc:sqlite:${dburi}", driver = "org.sqlite.JDBC")
        var count : Int = 0;
        transaction {
            InvData.select { (InvData.id eq uuid) and (InvData.invname eq invname_cmd) }.forEach {
                count++
            }
        }
        if (count != 0) return false

        transaction {
            create(InvData)
            InvData.insert {
                it[id] = uuid
                it[invname] = invname_cmd
                it[data] = item_data
            }
        }
        return true
    }
    fun selectinv_invname(p: Player, dburi: String) {
        val uuid = p.uniqueId.toString()
        Database.connect("jdbc:sqlite:${dburi}", driver = "org.sqlite.JDBC")
        var count: Int = 0
        var gui : Inventory = Bukkit.createInventory(null,54,"インベントリを選択してください")


        transaction {
            InvData.select { InvData.id eq uuid }.forEach {
                var item: ItemStack = ItemStack(Material.PAPER)
                val meta = item.itemMeta
                meta?.setDisplayName(it[InvData.invname])
                item.itemMeta = meta
                gui.setItem(count, item)
                count++
            }
        }
        p.openInventory(gui)
    }
    fun get_inv(p: Player, select_inv_name: String, dburi: String) {
        val uuid = p.uniqueId.toString()
        Database.connect("jdbc:sqlite:${dburi}", driver = "org.sqlite.JDBC")
        transaction {
            InvData.select { (InvData.id eq uuid) and (InvData.invname eq select_inv_name) }.forEach {
                val arr = it[InvData.data].split(",")
                var count: Int = 0
                for (i in arr) {
                    p.inventory.clear(count)
                    val serializedObject = Base64.getDecoder().decode(i)
                    val ByteArrayObject = ByteArrayInputStream(serializedObject)
                    val BukkitObject = BukkitObjectInputStream(ByteArrayObject)
                    val item: ItemStack? = BukkitObject.readObject() as ItemStack?
                    p.inventory.setItem(count, item)
                    count++
                }
            }
        }
        p.sendMessage("§e${select_inv_name}をロードしました")
    }
    fun removeinv(p: Player, invname_cmd: String, dburi: String): Boolean {
        val uuid = p.uniqueId.toString()
        Database.connect("jdbc:sqlite:${dburi}", driver = "org.sqlite.JDBC")
        var count : Int = 0;
        transaction {
            InvData.select { (InvData.id eq uuid) and (InvData.invname eq invname_cmd) }.forEach {
                count++
            }
        }
        if (count == 0) return false
        transaction {
            InvData.deleteWhere { (InvData.id eq uuid) and (InvData.invname eq invname_cmd) }
        }
        return true
    }
}