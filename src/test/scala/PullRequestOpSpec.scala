import org.scalatest.FunSpec
import scala.concurrent.Await

import codecheck.github.models.PullRequestInput
import java.util.Date

class PullRequestOpSpec extends FunSpec with Constants {

  describe("createPullRequest(owner, repo, input)") {
    val username = "shunjikonishi"
    val reponame = "test-repo"

    it("should success create and close") {
      val title = "Test Pull Request " + new Date().toString()
      val input = PullRequestInput(title, "githubapi-test-pr", "master", Some("PullRequest body"))
      val result = Await.result(api.createPullRequest(username, reponame, input), TIMEOUT)
      assert(result.title == title)
      assert(result.state == "open")

      val result2 = Await.result(api.closePullRequest(username, reponame, result.number), TIMEOUT)
      assert(result2.title == title)
      assert(result2.state == "closed")
    }

  }

}
