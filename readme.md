rappel commande :

sudo docker ps
sudo docker compose down
sudo docker compose up -d --remove-orphans
sudo docker compose -f tools.yml up -d
ngrok http 8080

sudo docker exec -u 0 -it jenkins git config --global --add safe.directory '*'
sudo docker exec -u 0 -it jenkins apt-get update
sudo docker exec -u 0 -it jenkins apt-get install -y docker.io