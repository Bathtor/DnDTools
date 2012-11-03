package com.larskroll.dndtools.utils

case class Spell(id: Long, name: String, desc: String, level: Int, school: String, ref: String)

class SpellList(val spells: List[Spell]) {

	def byLevel(level: Int) = new SpellList(spells filter { s => (s.level == level) });
	
	def orderByName() = new SpellList(spells sortWith (_.name < _.name));
}