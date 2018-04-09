package codecheck.github.operations

import java.net.URLEncoder
import scala.collection.immutable.Seq
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import org.json4s.JArray
import org.json4s.JString
import org.json4s.JNothing

import codecheck.github.api.GitHubAPI
import codecheck.github.exceptions.NotFoundException
import codecheck.github.models.BranchListItem
import codecheck.github.models.Branch

trait BranchOp {
  self: GitHubAPI =>

  def listBranches(owner: String, repo: String): Future[Seq[BranchListItem]] = {
    exec("GET", s"/repos/$owner/$repo/branches").map(
      _.body match {
        case JArray(arr) => arr.map(v => BranchListItem(v)).to[Seq]
        case _ => throw new IllegalStateException()
      }
    )
  }

  def getBranch(owner: String, repo: String, branch: String): Future[Option[Branch]] = {
    exec("GET", s"/repos/$owner/$repo/branches/${encode(branch)}", fail404=false).map{ res =>
      res.statusCode match {
        case 404 => None
        case 200 => Some(Branch(res.body))
      }
    }
  }

}
