FROM docker.io/eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.source=https://github.com/extact-io/msa-rms-apigateway

WORKDIR /msa-apigateway

COPY ./env/docker/docker-entrypoint.sh /usr/local/bin
COPY ./target/msa-rms-apigateway.jar ./
COPY ./target/libs ./libs

RUN chmod 755 /usr/local/bin/docker-entrypoint.sh

CMD ["docker-entrypoint.sh", "msa-rms-apigateway.jar"]

EXPOSE 7001
EXPOSE 7011
