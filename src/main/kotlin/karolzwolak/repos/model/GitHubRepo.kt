package karolzwolak.repos.model

import com.fasterxml.jackson.annotation.JsonProperty
import karolzwolak.repos.model.Branch

data class GitHubRepo(
    val name: String,
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val fork: Boolean,
    val owner: Owner,
)

data class GitHubRepoWithBranches(
    val repo: GitHubRepo,
    val branches: List<Branch>,
)
