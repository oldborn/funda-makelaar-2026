
## Description

This project is a coding challenge given by funda.nl. The aim is to find top 10 makelaars (realtors) using their API.
Given two requirement was to fetch top 10 realtors with most offers in Amsterdam and in Amsterdam with a garden (tuin).

### Selected technologies

- Java 21 and spring-boot 4.1.2 as general framework
- Reactive WebClient for developing the API Client
- Resilience4j RateLimiter to avoid very strict rate limiting policy (100 request per minute)
- Resilience4j Retry for handling various failures such as; TooManyRequests, InternalServerError, ServiceUnavailable, Unauthorized, Timeout and other connectivity issues
- Reactor Flux to collect the offers in parallel
- Java Streams for ranking
- Spring Shell for having a very small presentation layer
- Docker for running without having JVM technologies such as; JDK and Maven
- Wiremock, junit5F for testing

## Some remarks

- Given the limit of 100 request per minute, parallel execution of requests doesn't add much value. But, Flux makes it extremely easy to do parallel execution. Therefore, I have made some parallelism with 5 concurrent calls.
- Funda's API returns Unauthorized (401) instead of TooManyRequests (429) therefore added Retry for 401 as well.
- Requirement was always return top 10 with /amsterdam/ or /amsterdam/tuin/ filter. But, I decided to make it parameterized. This design choice mainly enable writing slimmer and more manageable unit tests rather than making the challenge more complex.

## AI Usage Disclaimer

In this project, I have used AI for mainly debuggin/fixing purposes. Therefore, code is almost entirely human written.
With the exception of two places: 
- ``private String renderTable(List<Realtor> realtors)`` to make the output prettier using ``TableModelBuilder``
- ``Dockerfile`` which was not required but I thought it would be helpful for people who don't want to install JDK and Maven

## How to run using Docker

#### Step 1: Build the docker image
```shell
    docker build -t funda-makelaar .
```

For deleting the image and re-building:

````shell
docker rmi -f funda-makelaar; docker build --no-cache -t funda-makelaar .
````

#### Step 2: Run 
- with Default Options (Amsterdam, Top 10)

``
    docker run --rm -e FUNDA_AANBOD_API_KEY="your_api_key_here" funda-makelaar
``

- with custom parameters

```shell
docker run --rm -e FUNDA_AANBOD_API_KEY="your_api_key_here" funda-makelaar top-realtors --city amsterdam --filter tuin --topN 10
```

## Final Words

It was so much fun to build this project.