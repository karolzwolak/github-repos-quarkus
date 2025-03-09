package karolzwolak.repos

import io.quarkus.test.junit.QuarkusTest
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers
import org.hamcrest.Matchers.greaterThan
import org.junit.jupiter.api.Test

@QuarkusTest
class GitHubResourceIT {

    companion object {
        private const val TEST_USERNAME = "octocat"
        private const val FORK_REPO_NAME = "linguist"

        private const val KNOWN_REPO_NAME = "Hello-World"
        private const val KNOWN_REPO_BRANCH_NAME = "master"
        private const val KNOWN_REPO_COMMIT_SHA = "7fd1a60b01f91b314f59955a4e4d4e80d8edf11d"

        private const val REPO_NAME_FIELD = "repo.name"
        private const val REPO_OWNER_LOGIN_FIELD = "repo.owner.login"

        private const val BRANCHES_FIELD = "branches"
        private const val BRANCH_NAME_FIELD = "name"

        private const val COMMIT_SHA_FIELD = "commit.sha"
        private val MAIN_BRANCH_NAMES = setOf("main", "master", "gh-pages")
    }

    @Test
    fun testGetRepos() {
        val response = given()
            .pathParam("username", TEST_USERNAME)
            .`when`()
            .get(FULL_REPOS_PATH)
            .then()
            .statusCode(200)
            .contentType(ContentType.JSON)

        validateCommonResponse(response)
        validateKnownRepository(response)
    }

    private fun validateCommonResponse(response: io.restassured.response.ValidatableResponse) {
        response
            .body("size()", greaterThan(0))
            .body("repo.fork", not(hasItem(true))) // Ensure no fork repos
            .body(REPO_NAME_FIELD, not(hasItem(FORK_REPO_NAME))) // Ensure specific fork repo is not present
            .body("[0].$REPO_NAME_FIELD", notNullValue())
            .body("[0].$REPO_OWNER_LOGIN_FIELD", equalTo(TEST_USERNAME))
            .body("[0].$BRANCHES_FIELD.size()", greaterThan(0))
            .body("[0].$BRANCHES_FIELD[0].$BRANCH_NAME_FIELD", notNullValue())
            .body("[0].$BRANCHES_FIELD[0].$COMMIT_SHA_FIELD", notNullValue())
            .body("$BRANCHES_FIELD.$BRANCH_NAME_FIELD", everyItem(hasItem(Matchers.`in`(MAIN_BRANCH_NAMES))))
    }

    private fun validateKnownRepository(response: io.restassured.response.ValidatableResponse) {
        response
            .body(REPO_NAME_FIELD, hasItem(KNOWN_REPO_NAME))
            .body(
                "find { it.$REPO_NAME_FIELD == '$KNOWN_REPO_NAME' }.$BRANCHES_FIELD.find { it.$BRANCH_NAME_FIELD == '$KNOWN_REPO_BRANCH_NAME' }.$COMMIT_SHA_FIELD",
                equalTo(KNOWN_REPO_COMMIT_SHA),
            )
    }
}
