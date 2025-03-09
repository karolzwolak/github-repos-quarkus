package karolzwolak.repos.exception

import jakarta.ws.rs.WebApplicationException
import jakarta.ws.rs.core.Response

class GitHubException(
    status: Response.Status,
    message: String,
) : WebApplicationException(
        Response
            .status(status)
            .entity(ErrorResponse(status.statusCode, message))
            .build(),
    )
