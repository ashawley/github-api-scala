package codecheck.github.operations

import scala.collection.immutable.Iterable
import scala.collection.immutable.Seq
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.Collaborator

trait CollaboratorOp {
  self: GitHubAPI =>

  def listCollaborators(owner: String,repo:String): Future[Iterable[Collaborator]] = {
    val path = s"/repos/${owner}/${repo}/collaborators"
    exec("GET",path).map(
      _.body match {
        case JArray(arr) => arr.map(v => Collaborator(v)).to[Seq]
        case _ => throw new IllegalStateException()
      }
    )
  }

  def isCollaborator(owner: String, repo: String, name: String): Future[Boolean] = {
    val path = s"/repos/${owner}/${repo}/collaborators/" + encode(name)
    exec("GET", path, fail404 = false).map { res =>
      res.statusCode match {
        case 404 => false
        case 204 => true
      }
    }
  }

  def addCollaborator(owner: String, repo: String, name: String): Future[Boolean] = {
    val path = s"/repos/${owner}/${repo}/collaborators/" + encode(name)
    exec("PUT", path).map {
      _.statusCode == 204
    }
  }

  def removeCollaborator(owner: String, repo: String, name: String): Future[Boolean] = {
    val path = s"/repos/${owner}/${repo}/collaborators/" + encode(name)
    exec("DELETE", path).map {
      _.statusCode == 204
    }
  }
}
