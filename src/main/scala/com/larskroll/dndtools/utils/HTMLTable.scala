package com.larskroll.dndtools.utils

class HTMLTable[T](val rows: Int, val cols: Int, left: (Int) => String, top: (Int) => String, f: (Int, Int) => T) {
	var top = true;
	var left = true;
	
	override def toString(): String = {
		var tStr = "<table>";
		if (top) {
			tStr += "<tr>";
			if (left) {
				tStr += "<td><b>" + left(-1) + "</b></td>";
			}
			for (i <- 0 until cols) {
				tStr += "<td><b>" + top(i) + "</b></td>";
			}
			tStr += "</tr>\n";
		}
		for (j <- 0 until rows) {
			tStr += "<tr>";
			if (left) {
				tStr += "<td><b>" + left(j) + "</b></td>";
			}
			for (i <- 0 until cols) {
				tStr += "<td>" + f(i, j).toString + "</td>";
			}
			tStr += "</tr>\n";
		}
		tStr += "</table>";
		return tStr;
	}
}

object HTMLTable {
	def apply[T](rows: Int, cols: Int, f: (Int,Int) => T): HTMLTable[T] = {
		val t = new HTMLTable[T](rows, cols, null, null, f);
		t.top = false;
		t.left = false;
		return t;
	}
}