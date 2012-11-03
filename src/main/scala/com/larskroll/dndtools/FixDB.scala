package com.larskroll.dndtools

import java.sql.DriverManager
import scalikejdbc.ConnectionPool
import java.sql.Connection
import anorm._

object FixDB extends App {
	override def main(args: Array[String]) = {
		// This is business as usual. I guess there's
		// also a "Scala way" to do this...?
		Class.forName("com.mysql.jdbc.Driver");
		ConnectionPool.add('dnd35, "jdbc:mysql://localhost/dnd35", "dnd", "dnd");
		ConnectionPool.add('dndtools, "jdbc:mysql://localhost/dndtools", "dnd", "dnd");
		implicit val conn: Connection = ConnectionPool('dnd35).borrow();
		val selectSpellLevels: SqlQuery = SQL("SELECT id, level FROM spell");
		val all: Stream[SqlRow] = selectSpellLevels.apply;
		all.foreach {
			case Row(id: Long, Some(level: String)) =>
				val newlevel = getMinLevel(level);
				SQL("UPDATE spell SET newlevel={lvl} WHERE id={id}").on("lvl" -> newlevel, "id" -> id).execute();
			case r => println("Didn't match " + r);
		}
	}

	private def getMinLevel(str: String): Int = {
		val splitComma = str.split(",");
		var newlevel = 9;
		splitComma.foreach(cl => {
			val splitSpace = cl.split(" ");
			if (splitSpace.length == 2) {
				val lvl = splitSpace(1).toInt;
				if (lvl < newlevel) {
					newlevel = lvl;
				}
			} else if (splitSpace.length == 3) {
				val lvl = splitSpace(2).toInt;
				if (lvl < newlevel) {
					newlevel = lvl;
				}
			} else {
				println("Got weird string: " + str + " => " + cl + " => ");
				splitSpace foreach println;
			}
		});
		return newlevel;
	}
}