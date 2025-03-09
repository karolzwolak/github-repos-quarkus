package karolzwolak.repos

import io.smallrye.mutiny.Uni
import jakarta.inject.Inject
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import karolzwolak.repos.exception.GitHubException
import karolzwolak.repos.model.GitHubRepoWithBranches

const val GITHUB_PATH = "/github"
const val REPOS_PATH = "/{username}/repos"
const val FULL_REPOS_PATH = GITHUB_PATH + REPOS_PATH

@Path(GITHUB_PATH)
class GitHubResource
    @Inject
    constructor(
        private val gitHubService: GitHubService,
    ) {
        @GET
        @Path(REPOS_PATH)
        @Produces(MediaType.APPLICATION_JSON)
        fun getRepos(
            @PathParam("username") username: String,
        ): Uni<List<GitHubRepoWithBranches>> =
            gitHubService
                .getNonForkReposWithBranches(username)
                .onFailure()
                .recoverWithUni { throwable: Throwable ->
                    when {
                        throwable is jakarta.ws.rs.WebApplicationException &&
                            throwable.response?.status == 404 -> {
                            Uni.createFrom().failure(GitHubException(Response.Status.NOT_FOUND, "User not found"))
                        }
                        throwable is jakarta.ws.rs.WebApplicationException -> {
                            val errorMessage = throwable.cause?.message ?: "An error occurred"
                            Uni.createFrom().failure(
                                GitHubException(
                                    Response.Status.fromStatusCode(throwable.response.status),
                                    "GitHub API error: $errorMessage",
                                ),
                            )
                        }
                        else -> {
                            Uni.createFrom().failure(
                                GitHubException(
                                    Response.Status.INTERNAL_SERVER_ERROR,
                                    throwable.message ?: "An unexpected error occurred",
                                ),
                            )
                        }
                    }
                }
    }
