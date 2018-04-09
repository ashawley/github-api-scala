package codecheck.github.utils

import scala.collection.immutable.Iterable
import scala.collection.immutable.Seq

case class PrintList(headers: String*) {
  def format(lens: Iterable[Int], row: Iterable[Any]): Unit = {
    lens.zip(row).foreach { case (n, s) =>
      print(s)
      print(" " * (n - s.toString.length))
    }
    println
  }

  def build(items: Iterable[Iterable[Any]]) = {
    if (items.size == 0) {
      println("No items")
    } else {
      val lens = items.foldLeft(
        headers.map(_.length)
      ) { (ret, row) =>
        ret.zip(row).map { case (n, s) => 
          Math.max(n, s.toString.length)
        }
      }.map(_ + 2)

      format(lens.to[Seq], headers.to[Seq])
      println("-" * lens.sum)
      items.foreach(row => format(lens.to[Seq],row))
   }
  }
}
