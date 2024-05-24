# allezon

## Setup (on RTB machines)

### Prerequisites

- Java 17
- Maven 3.6.3
- Docker 26.1.2 with DockerHub account
- Redeployed RTB machines

### Deployment Steps

1. Login to main host and kafka host
2. Fill the .env file with your credentials
3. Run deploy script

```bash
chmod u+x deploy_allezon.sh
./deploy_allezon.sh
```

## Architecture

This is the high-level architecture of the system that performed
best among all the tested configurations. You can easily change the number of instances
of each service by changing `hosts` files and `application.properties` accordingly.

![Architecture](images/allezon_diagram.png)

## Results

Some of the (most sensible) configurations we have tested:

| Swarm nodes | Kafka | Aerospike | Profiles compression | User Profile Score | Aggregate Score |
|-------------|-------|-----------|----------------------|--------------------|-----------------|
| 4           | 2     | 4         | FALSE                | 88.37              | 82.10           |
| 3           | 2     | 5         | FALSE                | 99.94              | 80.44           |
| 3           | 2     | 5         | TRUE                 | 100.00             | 99.71           |
| 4           | 2     | 4         | TRUE                 | 94.82              | 93.99           |

After testing various configurations, we found that our worst bottleneck was the amount of storage used by our services.
Initially, we added new Aerospike nodes, but unfortunately, it didn't help much, and we didn't want to reduce the nodes
of any service. So, we decided to compress the profiles, which helped significantly. However, it's still not enough to
have only four Aerospike nodes. Screenshots of the results are available in the `images` directory.

## Integration tests

Our system consists of multiple services, including two microservices (Backend, Aggregator), Aerospike database and Kafka Messaging Queue, which need to communicate with each other seamlessly.
To ensure that everything works as expected, we decided to incorporate integration tests as part of our solution and made use of the following libraries:
- `Testcontainers` -- to run Docker containers with Aerospike and Kafka
- `Spring Boot Test` -- to run and test Spring Boot applications
- `WireMock` -- to mock external services, such as Aggregator which acted as a client to Backend
- `JUnit` -- to run tests
- `RestAssured` -- to test if the API endpoints work as expected

To run the tests, simply execute the following command in the `allezon-backend` or `allezon-aggregator` directory:

```bash
mvn clean verify
```

## GitHub Actions

To automate the process of building, testing and deploying our services, we decided to use GitHub Actions.
In the `.github/workflows` directory, you can find the `maven.yml` file, which contains the configuration for the workflow
which is triggered on every push or pull request to the `main` branch. The main purpose of this workflow is to build and test
the services with maven. If the tests pass, the docker images are built and pushed to DockerHub.

Authors: Hubert Michalski & Aleksander Bloch