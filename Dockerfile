## $ docker run -d -p 7001:7001 -p 7011:7011 --rm --name rms-server rms-server
# 1st stage, build the app
FROM eclipse-temurin:17-jre-alpine
WORKDIR /msa-application

# Copy the binary built in the 1st stage
COPY ./target/msa-rms-application.jar ./
COPY ./target/libs ./libs

CMD ["java", "-jar", "msa-rms-application.jar"]

EXPOSE 7001
#EXPOSE 7011
