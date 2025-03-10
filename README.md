# About

Simple application that retrieves a user's public repositories from GitHub and their respective branches with their commit hashes.
Made with Quarkus in Kotlin.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./mvnw quarkus:dev
```

## Testing the application

```shell script
./mvnw test
```

## API Documentation

### Get User Repositories

```
GET /github/{username}/repos
```

#### Example Request

```
GET /github/octocat/repos
```

#### Example Response

Note: The repository 'fork' field is not included since only non-fork repositories are returned

```json
[
  {
    "repo": {
      "name": "Hello-World",
      "owner": {
        "login": "octocat"
      }
    },
    "branches": [
      {
        "name": "main",
        "commit": {
          "sha": "6dcb09b5b57875f334f61aebed695e2e4193db5e"
        }
      }
    ]
  }
]
```

#### Error Responses

- `404 Not Found`: User not found
- `500 Internal Server Error`: Unexpected internal error
- Other errors may be returned by the GitHub API

Example of such error:

```json
{
  "status": 403,
  "message": "GitHub API error: rate limit exceeded, status code 403"
}
```
