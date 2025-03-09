package karolzwolak.repos

import io.smallrye.mutiny.Uni
import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import karolzwolak.repos.model.GitHubRepo
import karolzwolak.repos.model.GitHubRepoWithBranches
import org.eclipse.microprofile.rest.client.inject.RestClient
import java.time.Duration

@ApplicationScoped
class GitHubService
    @Inject
    constructor(
        @RestClient private val gitHubClient: GitHubClient,
    ) {
        companion object {
            private const val TIMEOUT_SECONDS: Long = 10
            private const val MAX_RETRIES: Long = 3
        }

        fun getNonForkRepos(username: String): Uni<List<GitHubRepo>> =
            gitHubClient
                .getUserRepos(username)
                .map { repos -> repos.filter { !it.fork } }

        fun getNonForkReposWithBranches(username: String): Uni<List<GitHubRepoWithBranches>> =
            getNonForkRepos(username)
                .flatMap { nonForkRepos ->
                    Uni
                        .join()
                        .all(
                            nonForkRepos.map { repo -> convertToRepoWithBranches(repo) },
                        ).andCollectFailures()
                }

        private fun convertToRepoWithBranches(repo: GitHubRepo): Uni<GitHubRepoWithBranches> =
            gitHubClient
                .getRepoBranches(repo.owner.login, repo.name)
                .ifNoItem()
                .after(Duration.ofSeconds(TIMEOUT_SECONDS))
                .fail()
                .onFailure()
                .retry()
                .atMost(MAX_RETRIES)
                .map { branches -> GitHubRepoWithBranches(repo, branches) }
    }
