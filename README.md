
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

It was so much fun to build this project. And I promise to Kevin to read Dungeon Crawler Carl regardless of the outcome.

### Example run

````
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot ::                (v4.1.2)

2026-10-25T12:00:30.570+01:00  INFO 221964 --- [funda-makelaar-2026] [  restartedMain] i.g.o.f.FundaMakelaar2026Application     : Starting FundaMakelaar2026Application using Java 21.0.5 with PID 221964 (C:\repo\funda-makelaar-2026\target\classes started by ssafa in C:\repo\funda-makelaar-2026)
2026-10-25T12:00:30.573+01:00  INFO 221964 --- [funda-makelaar-2026] [  restartedMain] i.g.o.f.FundaMakelaar2026Application     : No active profile set, falling back to 1 default profile: "default"
2026-10-25T12:00:30.626+01:00  INFO 221964 --- [funda-makelaar-2026] [  restartedMain] .e.DevToolsPropertyDefaultsPostProcessor : Devtools property defaults active! Set 'spring.devtools.add-properties' to 'false' to disable
2026-10-25T12:00:32.121+01:00  INFO 221964 --- [funda-makelaar-2026] [  restartedMain] i.g.o.f.FundaMakelaar2026Application     : Started FundaMakelaar2026Application in 2.026 seconds (process running for 2.769)
shell:>top-realtors
2026-10-25T12:00:41.615+01:00  WARN 221964 --- [funda-makelaar-2026] [ctor-http-nio-2] i.g.o.f.c.f.FundaAanbodApiClient         : attempt 1/4 failed, retry in 8 seconds. cause: HTTP Call failed with status code: 503 status message: Service Unavailable
2026-10-25T12:01:36.524+01:00  WARN 221964 --- [funda-makelaar-2026] [ctor-http-nio-6] i.g.o.f.c.f.FundaAanbodApiClient         : attempt 1/4 failed, retry in 8 seconds. cause: HTTP Call failed with status code: 401 status message: Unauthorized

┌────┬──────────┬────────────────────────────────────┬──────┐
│Rank│Realtor ID│Realtor Name                        │Offers│
├────┼──────────┼────────────────────────────────────┼──────┤
│1   │24648     │Heeren Makelaars                    │201   │
│2   │24067     │Broersma Wonen                      │137   │
│3   │24605     │Hallie & Van Klooster Makelaardij   │118   │
│4   │24705     │Eefje Voogd Makelaardij             │117   │
│5   │24592     │Ramón Mossel Makelaardij o.g. B.V.  │114   │
│6   │24599     │DSTRCT | Forbes Global Properties   │104   │
│7   │24079     │Makelaardij Van der Linden Amsterdam│76    │
│8   │24789     │VON POLL REAL ESTATE                │71    │
│9   │15406     │Openbare Makelaardij                │69    │
│10  │24607     │KRK Makelaars Amsterdam             │67    │
└────┴──────────┴────────────────────────────────────┴──────┘

shell:>top-realtors --filter tuin

┌────┬──────────┬───────────────────────────────────────────┬──────┐
│Rank│Realtor ID│Realtor Name                               │Offers│
├────┼──────────┼───────────────────────────────────────────┼──────┤
│1   │24067     │Broersma Wonen                             │30    │
│2   │24594     │Hoekstra en Van Eck Amsterdam Noord        │20    │
│3   │24648     │Heeren Makelaars                           │20    │
│4   │24599     │DSTRCT | Forbes Global Properties          │18    │
│5   │24065     │Carla van den Brink B.V.                   │17    │
│6   │24605     │Hallie & Van Klooster Makelaardij          │15    │
│7   │12285     │Makelaarsland                              │15    │
│8   │60557     │Linger OG Makelaars en Taxateurs           │14    │
│9   │24819     │Fransen & Kroes Makelaars Amsterdam        │10    │
│10  │24550     │Van de Steege Makelaarsgroep Amsterdam-West│9     │
└────┴──────────┴───────────────────────────────────────────┴──────┘

shell:>
````