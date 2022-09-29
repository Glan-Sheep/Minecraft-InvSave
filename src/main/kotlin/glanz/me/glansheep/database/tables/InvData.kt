package glanz.me.glansheep.database.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Table.Dual.uuid
import java.util.UUID

object InvData : Table("inv") {
    val id = text("playername")
    val invname = text("invname")
    val data = text("data")
}