#!/bin/bash -x
if [ -f /usr/local/apps/msa-apigateway/secrets/env.product ]; then
    . /usr/local/apps/msa-apigateway/secrets/env.product
fi
if [ -f /usr/local/apps/msa-apigateway/deployment/image_tag ]; then
    IMAGE_TAG=`cat /usr/local/apps/msa-apigateway/deployment/image_tag`
fi

echo "[msa-apigateway]STARTING..."

END_STATUS=0

# Create docker network
docker network create -d bridge msa-apigateway-network  || {
    	echo "msa-apigateway-network create fail error:$?" && END_STATUS=1
    }

# Start msa-apigateway
docker run -d \
    -p 80:7001 \
    -p 443:7011 \
    -v /etc/letsencrypt/live/api.rms.extact.io/api_rms_extact_io.p12:/resources/api_rms_extact_io.p12 \
    -v /usr/local/apps/msa-apigateway/secrets/jwt.prod.key:/resources/jwt.prod.key \
    -v /usr/local/apps/msa-apigateway/secrets/jwt.prod.pub.key:/resources/jwt.prod.pub.key \
    -v /usr/local/apps/msa-apigateway/deployment/logback-production.xml:/resources/logback-production.xml \
    -v /usr/local/apps/msa-apigateway/logs:/logs \
    -e ENV_RMS_SEC_JWT_SECRETKEY=/resources/jwt.prod.key \
    -e ENV_RMS_SEC_JWT_PUBLICKEY=/resources/jwt.prod.pub.key \
    -e ENV_RMS_SEC_TLS_FILE=/resources/api_rms_extact_io.p12 \
    -e ENV_RMS_SEC_TLS_PASSPHRASE=$ENV_RMS_SEC_TLS_PASSPHRASE \
    -e ENV_RMS_SEC_JWT_FILTER=true \
    -e ENV_RMS_SERVICE_URL_ITEM=http://item-service.local:7002 \
    -e ENV_RMS_SERVICE_URL_RESERVATION=http://reservation-service.local:7003 \
    -e ENV_RMS_SERVICE_URL_USER=http://user-service.local:7004 \
    -e ENV_RMS_CORS_ALLOW_ORIGINS=https://app.rms.extact.io \
    -e LOGBACK_CONFIG_PATH=/resources/logback-production.xml \
    -e TZ=Asia/Tokyo \
    -e TRACING_ENABLED=true \
    -e TRACING_HOST=jaeger \
    --name msa-apigateway --rm \
    --network msa-apigateway-network \
    ghcr.io/extact-io/msa-apigateway:$IMAGE_TAG || {
    	echo "msa-apigateway start fail error:$?" && END_STATUS=1
    }

# Start jaeger
docker run -d --name jaeger \
    -e COLLECTOR_ZIPKIN_HOST_PORT=:9411 \
    -e COLLECTOR_OTLP_ENABLED=true \
    -p 6831:6831/udp \
    -p 5778:5778 \
    -p 16686:16686 \
    -p 14250:14250 \
    -p 14268:14268 \
    -p 9411:9411 \
    --name jaeger --rm \
    --network msa-apigateway-network \
  	jaegertracing/all-in-one1:1.39 || {
    	echo "jaeger start fail error:$?" && END_STATUS=1  
    }

exit $END_STATUS
