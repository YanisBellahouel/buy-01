FROM jenkins/jenkins:lts
USER root

# 1. Installer Git et Docker
RUN apt-get update && apt-get install -y git docker.io curl

# 2. Installer Docker Compose (le plugin)
RUN mkdir -p /usr/lib/docker/cli-plugins && \
    curl -SL https://github.com/docker/compose/releases/download/v2.24.2/docker-compose-linux-x86_64 -o /usr/lib/docker/cli-plugins/docker-compose && \
    chmod +x /usr/lib/docker/cli-plugins/docker-compose

# 3. Configurer Git pour éviter l'erreur "dubious ownership"
RUN git config --global --add safe.directory '*'

USER jenkins