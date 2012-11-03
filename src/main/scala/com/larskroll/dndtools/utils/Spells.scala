package com.larskroll.dndtools.utils

import IntegerMath._

object Spells {
	def words(level: Int): Int = {
		return pow((level + 1), 2);
	}

	def forLevel(sl: Int, cl: Int): Int = {
		val z = cl - sl * 2 + 2;
		if (z < 0) {
			return 0;
		} else {
			return min(z + 2, 6);
		}
	}

	def forSecondaryLevel(sl: Int, cl: Int): Int = {
		val z = cl - sl * 2;
		if (z < 0) {
			return 0;
		} else {
			return min(root(z + 16, 2), 6);
		}
	}

	def forTertiaryLevel(sl: Int, cl: Int): Int = {
		val z = cl - sl * 2;
		if (z < 0) {
			return 0;
		} else {
			return min(log(z + 1, 4), 6);
		}
	}

	def wordsForLevel(cl: Int): Int = ((0 to 9) map { i => forLevel(i, cl) * words(i) }) sum;
	def wordsForSecondaryLevel(cl: Int): Int = ((0 to 9) map { i => forSecondaryLevel(i, cl) * words(i) }) sum;
	def wordsForTertiaryLevel(cl: Int): Int = ((0 to 9) map { i => forTertiaryLevel(i, cl) * words(i) }) sum;

	def maxSLforLevel(cl: Int): Int = ((9 to 0 by -1) filter { i => forLevel(i, cl) != 0 }) max;
	def maxSLforSecondaryLevel(cl: Int): Int = ((9 to 0 by -1) filter { i => forSecondaryLevel(i, cl) != 0 }) max;
	def maxSLforTertiaryLevel(cl: Int): Int = ((9 to 0 by -1) filter { i => forTertiaryLevel(i, cl) != 0 }) max;

	def primaryTable(): String = {
		val sl = (col: Int) => ord(col);
		val level = (col: Int, row: Int) => forLevel(col, row + 1);
		val t = new HTMLTable(20, 10, cl, sl, level);
		return t.toString;
	}

	def secondaryTable(): String = {
		val sl = (col: Int) => ord(col);
		val level = (col: Int, row: Int) => forSecondaryLevel(col, row + 1);
		val t = new HTMLTable(20, 10, cl, sl, level);
		return t.toString;
	}

	val ord = (i: Int) => i match {
		case 0 => "0";
		case 1 => "1st";
		case 2 => "2nd";
		case 3 => "3rd";
		case x => x + "th";
	}

	val cl = (row: Int) => row match {
		case -1 => "Level";
		case x => ord(x + 1);
	}

	def wordTable(): String = {
		val wrd = (col: Int) => col match {
			case 0 => "Pri. Words";
			case 1 => "Pri. MaxSL";
			case 2 => "Sec. Words";
			case 3 => "Sec. MaxSL";
			case 4 => "Ter. Words";
			case 5 => "Ter. MaxSL";
		}
		val level = (col: Int, row: Int) => col match {
			case 0 => wordsForLevel(row + 1);
			case 1 => maxSLforLevel(row + 1);
			case 2 => wordsForSecondaryLevel(row + 1);
			case 3 => maxSLforSecondaryLevel(row + 1);
			case 4 => wordsForTertiaryLevel(row + 1);
			case 5 => maxSLforTertiaryLevel(row + 1);
		}
		val t = new HTMLTable(20, 6, cl, wrd, level);
		return t.toString;
	}
}