
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