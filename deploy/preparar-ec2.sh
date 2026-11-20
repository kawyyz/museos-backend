#!/usr/bin/env bash
# Prepara una instancia Ubuntu 24.04 recién creada: Docker, Docker Compose, swap y carpeta de trabajo.
# Uso (dentro de la instancia):  bash preparar-ec2.sh
set -euo pipefail

sudo apt-get update
sudo apt-get install -y docker.io docker-compose-v2
sudo systemctl enable --now docker
sudo usermod -aG docker "$USER"

# Memoria de respaldo de 2 GB, útil en instancias pequeñas
if ! swapon --show | grep -q swapfile; then
  sudo fallocate -l 2G /swapfile
  sudo chmod 600 /swapfile
  sudo mkswap /swapfile
  sudo swapon /swapfile
  echo '/swapfile none swap sw 0 0' | sudo tee -a /etc/fstab
fi

sudo mkdir -p /opt/museos
sudo chown "$USER":"$USER" /opt/museos

echo
echo "Listo. Cierra la sesión SSH y vuelve a entrar para que el grupo docker tenga efecto."
