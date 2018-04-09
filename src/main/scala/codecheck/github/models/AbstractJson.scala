package codecheck.github.models

import scala.collection.immutable.Iterable
import scala.collection.immutable.Seq
import org.json4s.JValue
import org.json4s.JNothing
import org.json4s.JNull
import org.json4s.JObject
import org.json4s.JArray
import org.json4s.Formats
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods
import codecheck.github.utils.Json4s.formats

import org.joda.time.DateTime

class AbstractJson(value: JValue) {

  def opt(path: String): Option[String] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => None
      case JNull => None
      case v: JValue => Some(v.extract[String])
    }
  }

  def get(path: String) = opt(path).get

  def dateOpt(path: String): Option[DateTime] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => None
      case JNull => None
      case v: JValue => Some(v.extract[DateTime])
    }
  }

  def getDate(path: String): DateTime = dateOpt(path).get

  def booleanOpt(path: String): Option[Boolean] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => None
      case JNull => None
      case v: JValue => Some(v.extract[Boolean])
    }
  }

  def boolean(path: String): Boolean = booleanOpt(path).get

  def objectOpt[T](path: String)(f: JValue => T): Option[T] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case x: JObject => Some(f(x))
      case _ => None
    }
  }

  override def toString = JsonMethods.pretty(value)

  def seqOpt[T](path: String): Iterable[T] = {
    path.split("\\.").foldLeft(value) { (v, s) =>
      v \ s
    } match {
      case JNothing => Seq.empty[T]
      case JNull => Seq.empty[T]
      case v: JArray => v.values.to[Seq].map(_.asInstanceOf[T])
      case v: JValue => Seq(v.asInstanceOf[T])
    }
  }

  def seq(path: String): Iterable[String] = seqOpt(path)
}
