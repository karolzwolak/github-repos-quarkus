package karolzwolak.repos

import io.smallrye.mutiny.Uni
import jakarta.ws.rs.GET
import jakarta.ws.rs.Path
import jakarta.ws.rs.PathParam
import jakarta.ws.rs.Produces
import jakarta.ws.rs.core.MediaType
import karolzwolak.repos.model.Branch
import karolzwolak.repos.model.GitHubRepo
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient

@RegisterRestClient(baseUri = "https://api.github.com")
interface GitHubClient {
    @GET
    @Path("/users/{username}/repos")
    @Produces(MediaType.APPLICATION_JSON)
    fun getUserRepos(
        @PathParam("username") username: String,
    ): Uni<List<GitHubRepo>>

    @GET
    @Path("/repos/{owner}/{repo}/branches")
    @Produces(MediaType.APPLICATION_JSON)
    fun getRepoBranches(
        @PathParam("owner") owner: String,
        @PathParam("repo") repo: String,
    ): Uni<List<Branch>>
}
