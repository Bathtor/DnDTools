package com.larskroll.dndtools

import akka.actor.Actor
import scalikejdbc.ConnectionPool
import java.sql.Connection
import anorm.SqlParser._
import anorm._
import com.larskroll.dndtools.utils.Spell
import com.larskroll.dndtools.utils.SpellList
import com.larskroll.dndtools.utils.Spells


case object GetSpells
case class GetWords(key: Long)
case class GetSpell(id: Long)
case class GetMemorised(key: Long)
case class ToggleSpell(spellId: Long, key: Long)
abstract class ToggleReply(inserted: Boolean)
case object EntryInserted extends ToggleReply(true);
case object EntryDeleted extends ToggleReply(false);
case class Words(num: Int)

class SpellService extends Actor {
	Class.forName("com.mysql.jdbc.Driver");
	ConnectionPool.add('dnd35, "jdbc:mysql://localhost/dnd35", "dnd", "dnd");
	implicit val conn: Connection = ConnectionPool('dnd35).borrow();

	def receive = {
		case GetSpells => {
			val selectSpellLevels: SqlQuery = SQL("SELECT id, name, newlevel, short_description, school, reference FROM spell WHERE newlevel IS NOT NULL");
			//val all: Stream[SqlRow] = selectSpellLevels();
			val spells = selectSpellLevels.parse(spellParser *);
			val spellList: SpellList = new SpellList(spells);
			sender ! spellList;
		}
		case GetWords(key) => {
			val selectMemSpells = SQL("SELECT newlevel FROM memorised m LEFT JOIN spell s ON m.spell_id = s.id WHERE url_key = {key}").on("key" -> key);
			val memSpells = selectMemSpells.parse(int("newlevel") *)
			val words = memSpells map (i => Spells.words(i)) sum;
			sender ! Words(words);
		}
		case GetSpell(id) => {
			val selectSpell = SQL("SELECT id, name, newlevel, short_description, school, reference FROM spell WHERE id={id}").on("id" -> id);
			val spell = selectSpell.parse(spellParser.singleOpt);
			sender ! spell;
		}
		case GetMemorised(key) => {
			val selectMemSpells = SQL("SELECT spell_id FROM memorised WHERE url_key = {key}").on("key" -> key);
			val memSpells = selectMemSpells.parse(long("spell_id") *)
			sender ! memSpells;
		}
		case ToggleSpell(spellId, key) => {
			val selectCheck = SQL("SELECT count(*) FROM memorised WHERE spell_id = {id} AND url_key = {key}").on("id" -> spellId, "key" -> key);
			val check = selectCheck.parse(long("count(*)").single);
			if (check > 0) {
				val delete = SQL("DELETE FROM memorised WHERE spell_id = {id} AND url_key = {key}").on("id" -> spellId, "key" -> key);
				delete.execute();
				sender ! EntryDeleted;
			} else {
				val insert = SQL("INSERT INTO memorised (spell_id, url_key) VALUE ({id}, {key})").on("id" -> spellId, "key" -> key);
				insert.execute();
				sender ! EntryInserted;
			}
		}
	}
	
	val spellParser = long("id") ~ 
	str("name") ~ 
	get[Option[Int]]("newlevel") ~ 
	get[Option[String]]("short_description") ~
	get[Option[String]]("school") ~
	get[Option[String]]("reference") map {
		case id ~ name ~ newlevel ~ desc ~ school ~ ref =>
			Spell(id, name, desc.getOrElse(""), newlevel.getOrElse(-1), school.getOrElse("-"), ref.getOrElse("?"))
	}
}
