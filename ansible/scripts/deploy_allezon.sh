chmod u+x ./*.sh

./install_ansible.sh
./add_machines_to_known_hosts.sh
./install_aerospike.sh
./install_docker.sh
./install_kafka.sh
./create_kafka_topic.sh
./build_images.sh
./add_services_to_docker_swarm.sh