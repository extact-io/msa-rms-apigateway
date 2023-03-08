FROM docker.io/eclipse-temurin:17-jre-alpine

LABEL org.opencontainers.image.source=https://github.com/extact-io/msa-rms-apigateway

WORKDIR /msa-apigateway

COPY ./target/msa-rms-apigateway.jar ./
COPY ./target/libs ./libs

CMD ["java", "-jar", "msa-rms-apigateway.jar"]

EXPOSE 7001
EXPOSE 7011
