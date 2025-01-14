# Funpokedex

Funpokedex is a pokedex in the form of a REST API that returns pokèmon information.
Sometimes, pokemon's descriptions might be weird.

## Technologies used

- Java 21
- Micronaut framework version 4.7.4
- Gradle

## Api documentation
After starting the application, swagger is visible [here](localhost:8080/swagger-ui)

**I am assuming you are running the application locally on port 8080 before pressing this link.**

## Features
- Caching is enabled by default and mandatory, as-per fair use policy of the api services that this application uses.
- Avoid making calls to apis with nonsense input
- Gracefully handles client and apis errors by returning pokèmon easter eggs with explanations.
- Uses [Reactor](https://projectreactor.io/docs/core/release/reference/gettingStarted.html).

## Getting started

### Prerequisites
To run this project, you need java 21 installed in your machine and properly configured.
A dockerfile is provided to run it using docker to.

#### Running with docker

In order to run this application using docker, you need docker on your machine and the docker daemon running.
Then you can build the image by using this command in project folder:

`docker build -t funpokedex.latest .`

After it finishes, in order to run it on port 8080 you need to run this command:

`docker run -p 8080:8080 funpokedex`

#### Starting the application

If you wish to not use docker and satisfy the prerequisites, you can build and run it with this commands:

```bash
./gradlew build
./gradlew run
```

If you made some edits and during the build spotlessCheck fails, you need to run this in order to format the program properly

`./gradlew spotlessJavaApply`

You can then retry the build and run commands.
Micronaut also supports live reloading through the command:

`./gradlew run --continuous`

You can also generate a native executable by following [these](https://guides.micronaut.io/latest/micronaut-http-client-gradle-java.html#generate-a-micronaut-application-native-executable-with-graalvm) 
steps using GraalVM.

## Usage

### Endpoint 1 - Basic pokèmon infomration
Given a Pokemon name, returns standard Pokemon description and additional information.

#### Example call (using httpie):
`http http://localhost:8080/pokemon/mewtwo`

#### Example response
```json
{
  "name": "mewtwo",
  "description": "It was created by a scientist after years of horrific gene splicing and DNA engineering experiments.",
  "habitat": "rare",
  "isLegendary": true
}
```

### Endpoint 2 - Translated pokèmon description
Given a Pokèmon name, return translated Pokèmon description and other basic information using
the following rules:
1. If the Pokèmon’s habitat is cave or it’s a legendary Pokèmon then apply the Yoda translation.
2. For all other Pokèmon, apply the Shakespeare translation.
3. If you can’t translate the Pokèmon’s description (for whatever reason 😉) then use the standard
   description

#### Example call (using httpie):
`HTTP/GET /pokemon/translated/<pokemon name>`

#### Example response:

```json
{
  "name": "mewtwo",
  "description": "Created by a scientist after years of horrific gene splicing and dna engineering experiments, it was.",
  "habitat": "rare",
  "isLegendary": true
}
```

## Documentation

Check out this useful documentation I followed during the development process:

- [Creating an http client in micronaut](https://guides.micronaut.io/latest/micronaut-http-client-gradle-java.html)
- [Micronaut HTTP client documentation](https://docs.micronaut.io/latest/guide/#httpClient)
- [Caching documentation](https://docs.micronaut.io/latest/guide/#caching)
- [PokeAPI documentation](https://pokeapi.co/docs/v2)
- [FuntranslationsAPI documentation](https://funtranslations.com/api/)

## Things I would have done in real production and different design decision I'd take

Scaling an application isn't an easy task.
Starting from code, I'd probably spend more time to study the layout of the application. 
I feel like it can and should be better.

I'd also try to study a way to make divide test file in more files. It seems to work good and test are high value to me,
but it can probably be made more readable.

About the architecture, this application implements caching as per fair use policy of some of the apis I am using
and because it simply was just the right thing to do in this kind of task. 

To ensure things work smoother and are as scalable as possible, possibilities are endless.
With more time and energy to invest in this, I'd probably study a smart implementation of at least these things:
- Retry pattern
- Circuit breaking
- Rate limiting requests

At the start I was planning to implement at least the retry pattern and also tried to thinker with circuit breaker, but I felt like I had no time to properly configure it
in order to work in a smart way with the cache I implemented.

Maybe I was wrong, but that's how it turned out in the end.

Another thing I'd do different is to gather precise data about all pokèmon's name in existance at the moment and send requests only if the pokèmon exists

instead of using regex in order to exclude some of the inputs.

I'd probably also send proper error responses instead of pokèmon, but thought it was fun to put some easter egg into this project and thought "why not".

I'll be skipping parts about avoiding single-points of failure and scaling in a more "infrastructural" sense because I think that's not in scope right now.

I think that's it, if I can think of something else we can discuss it in the next step if I am successful in thins take home :).
