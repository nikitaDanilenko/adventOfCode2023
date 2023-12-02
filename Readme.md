# Solutions for the Advent of Code 2023 puzzles

## How to run

From the project's parent directory call `./gradlew run`.
Doing so starts an HTTP server, where the default port is 9000,
but this is configurable via the environment variable `KTOR_DEPLOYMENT_PORT`,
e.g. `KTOR_DEPLOYMENT_PORT=<my-port> ./gradlew run`.

Each solution receives the input via the `day<xx>` endpoint, where values below 10 are zero-padded.
The input type is `text/plain`, and should not contain a trailing newline.
The output is a JSON object containing both answers.

# Personal takeaways

1. `gradlew` seems to have issues with `--continuous`, which makes development tedious.
2. The Kotlin syntax is very terse, especially coming from Scala.
   The different braces (for functions, and lambdas) definitely require some exercise. 