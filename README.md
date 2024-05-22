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

![Architecture](images/allezon_diagram.png)

Authors: Hubert Michalski & Aleksander Bloch