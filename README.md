# GitHubAPI for scala
GitHubAPI wrapper for scala

## Dependencies
- json4s
- async-http-client

## How to develop
To develop this, you have to get GitHub API Token.  
You can get it from [here](https://github.com/settings/applications).

``` bash
export GITHUB_TOKEN=[Your GitHub Token] 
git clone git@github.com:code-check/github-api.git
cd github-api
sbt test
```

## About models
We don't aim to define all fields of JSON.
Because these are too much and might be changed by GitHub.

We just define the fields we need.

## License
MIT