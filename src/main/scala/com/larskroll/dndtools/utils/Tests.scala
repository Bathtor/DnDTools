package com.larskroll.dndtools.utils

import IntegerMath._

object Tests extends App {
	
	override def main(args: Array[String]) = {
		//tableTest(5,6);
		println(Spells.wordTable);
//		rootTest();
//		logTest();
	}
	
	def tableTest(rows: Int, cols: Int): Unit = {
		val mat = (i: Int, j: Int) => {
			i + "," + j;
		}
		val ident = (i: Int) => {
			i.toString;
		}
		println("----Table without headers----- \n");
		val noHeaderT = HTMLTable[String](rows, cols, mat);
		println(noHeaderT);
		println("----Table without headers----- \n");
		val headerT = new HTMLTable[String](rows, cols, ident, ident, mat);
		println(headerT);
	}
	
	def rootTest() = {
		println("Squareroot of 4 should be 2 and is " + root(4, 2));
		println("4th-root of 16 should be 2 and is " + root(16, 4));
		println("3rd-root of 27 should be 3 and is " + root(27, 3));
		println("3rd-root of 26 should be 3 and is " + root(26, 3));
		println("3rd-root of 9 should be 2 and is " + root(9, 3));
	}
	
	def logTest() = {
		println("log2 of 8 should be 3 and is " + log(8, 2));
		println("log2 of 511 should be 9 and is " + log(511, 2));
		println("log10 of 10 should be 1 and is " + log(10, 10));
		println("log10 of 100 should be 2 and is " + log(100, 10));
		println("log10 of 99 should be 2 and is " + log(99, 10));
		println("log10 of 11 should be 1 and is " + log(11, 10));
		println("log10 of 9 should be 1 and is " + log(9, 10));
		println("log10 of 2 should be 0 and is " + log(2, 10));
		println("log10 of 1 should be 0 and is " + log(1, 10));
	}
}