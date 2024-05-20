# allezon

## Setup (on RTB machines)

1. Install Ansible and sshpass

```bash
ssh <username>@<username>vm101.rtb-lab.pl
sudo apt -y install ansible sshpass
sudo add-apt-repository ppa:ansible/ansible
sudo apt update
sudo apt upgrade ansible
```

2. Add all machines to known hosts

```bash
for i in `seq -w 01 10`; do sshpass -p <password> ssh <username>@<username>vm1$i.rtb-lab.pl -o StrictHostKeyChecking=no -C "/bin/true"; done
```

3. Install Areospike, Docker and Kafka (see scripts directory)

```bash
# WARN: change hosts and scripts accordingly to match your credentials
./install_aerospike.sh
./install_docker.sh
./install_kafka.sh
```

4. Create kafka topic (remember about partitions)

```bash
# kafka is on vm107 and vm108
ssh <username>@<username>vm107.rtb-lab.pl
/opt/kafka/bin/kafka-topics.sh --create --topic user-tag-topic --partitions 10 --bootstrap-server localhost:9092
```

5. Create container images

```bash
# WARN: change application.properties accordingly
./build_images.sh
```

6. Run containers via Docker Swarm

```bash
# docker swarm is on vm101
ssh <username>@<username>vm101.rtb-lab.pl
sudo docker service create --name allezon-backend -p 8080:8080 <dockerhub_username>/allezon-backend
sudo docker service create --name allezon-aggregator -p 9090:9090 <dockerhub_username>/allezon-aggregator
```
