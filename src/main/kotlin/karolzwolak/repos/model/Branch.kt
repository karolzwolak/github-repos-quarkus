package karolzwolak.repos.model

data class Branch(
    val name: String,
    val commit: Commit,
)

data class Commit(
    val sha: String,
)
