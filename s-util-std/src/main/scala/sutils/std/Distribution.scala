package sutils.std

import java.util.Random

object Distribution {
  
  def uniform(size: Int, max: Double)(implicit r: Random): Array[Double] = {
    val x = new Array[Double](size)
    var i = 0
    while(i < size) {
      x(i) = r.nextDouble() * max
      i += 1
    }
    x
  }

  def gaussian(
    mean: Double,
    stdDev: Double
  )(size: Int)(implicit r: Random): Array[Double] = {

    val variance = stdDev * stdDev
    val x = new Array[Double](size)
    var i = 0
    while(i < size) {
      x(i) = (r.nextGaussian() + mean) * variance
      i += 1
    }
    x
  }

  def gaussian(size: Int)(implicit r: Random): Array[Double] = {
    val x = new Array[Double](size)
    var i = 0
    while(i < size) {
      x(i) = r.nextGaussian()
      i += 1
    }
    x
  }

}