package codecheck.github.operations

import scala.collection.immutable.Iterable
import scala.collection.immutable.Seq
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Label
import codecheck.github.models.LabelInput

trait LabelOp {
  self: GitHubAPI =>

  private def doLabels(method: String, owner: String, repo: String, number: Long, labels: Iterable[String]): Future[Iterable[Label]] = {
    val path = s"/repos/$owner/$repo/issues/$number/labels"
    val body = if (method == "GET") {
      JNothing
    } else {
      JArray(labels.map(JString(_)).toList)
    }
    exec(method, path, body).map {
      _.body match {
        case JArray(arr) => arr.map(v => Label(v)).to[Seq]
        case _ => throw new IllegalStateException()
      }
    }
  }

  def addLabels(owner: String, repo: String, number: Long, labels: String*): Future[Iterable[Label]] = {
    doLabels("POST", owner, repo, number, labels.to[Seq])
  }

  def replaceLabels(owner: String, repo: String, number: Long, labels: String*): Future[Iterable[Label]] = {
    doLabels("PUT", owner, repo, number, labels.to[Seq])
  }

  def removeAllLabels(owner: String, repo: String, number: Long): Future[Iterable[Label]] = {
    doLabels("PUT", owner, repo, number, Seq.empty[String])
  }

  def removeLabel(owner: String, repo: String, number: Long, label: String): Future[Iterable[Label]] = {
    val path = s"/repos/$owner/$repo/issues/$number/labels/" + encode(label)
    exec("DELETE", path).map {
      _.body match {
        case JArray(arr) => arr.map(v => Label(v)).to[Seq]
        case _ => throw new IllegalStateException()
      }
    }
  }

  def listLabels(owner: String, repo: String, number: Long): Future[Iterable[Label]] = {
    doLabels("GET", owner, repo, number, Seq.empty[String])
  }

  def listLabelDefs(owner: String, repo: String): Future[Iterable[Label]] = {
    val path = s"/repos/$owner/$repo/labels"
    exec("GET", path).map {
      _.body match {
        case JArray(arr) => arr.map(v => Label(v)).to[Seq]
        case _ => throw new IllegalStateException()
      }
    }
  }

  def getLabelDef(owner: String, repo: String, label: String): Future[Option[Label]] = {
    val path = s"/repos/$owner/$repo/labels/" + encode(label)
    exec("GET", path, fail404=false).map(res => 
      res.statusCode match {
        case 404 => None
        case 200 => Some(Label(res.body))
      }
    )
  }

  def createLabelDef(owner: String, repo: String, label: LabelInput): Future[Label] = {
    val path = s"/repos/$owner/$repo/labels"
    exec("POST", path, label.value).map(res => Label(res.body))
  }

  def updateLabelDef(owner: String, repo: String, name: String, label: LabelInput): Future[Label] = {
    val path = s"/repos/$owner/$repo/labels/" + encode(name)
    exec("PATCH", path, label.value).map(res => Label(res.body))
  }

  def removeLabelDef(owner: String, repo: String, name: String): Future[Boolean] = {
    val path = s"/repos/$owner/$repo/labels/" + encode(name)
    exec("DELETE", path).map {
      _.statusCode == 204
    }
  }
}
