package com.larskroll.dndtools.utils

class Overflow extends Exception;

object IntegerMath {
	//Computes x^i on ints
	def pow(x: Int, i: Int): Int = {
		if (i == 0) {
			return 1;
		} else if (i % 2 == 0) {
			val x2 = pow(x, i/2);
			val x22 = x2*x2;
			if (x22 < x2) {
				throw new Overflow;
			} else {
				return x22;
			}
		} else {
			val x2 = pow(x, i - 1);
			val xx2 = x*x2;
			if (xx2 < x2) {
				throw new Overflow;
			} else {
				return xx2;
			}
		}
	}
	
	//Computes root_i(x) on ints 
	def root(x: Int, i: Int): Int = {
		assert(x >= 0);
		val r = scala.math.pow(x.toDouble, 1d/i);
		return scala.math.round(r).toInt;
	}
	
	//Computes log_i(x) on ints 
	def log(x: Int, i: Int): Int = {
		assert(x >= 1);
		val l = scala.math.log(x.toDouble)/scala.math.log(i);
		return scala.math.round(l).toInt;
	}
	
	def min(x: Int, y: Int): Int = if (x < y) x else y;
	def max(x: Int, y: Int): Int = if (x > y) x else y;
	
	
}