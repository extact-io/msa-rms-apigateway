#!/bin/bash
if [ -f /usr/local/apps/msa-apigateway/secrets/env.product ]; then
    . /usr/local/apps/msa-apigateway/secrets/env.product
fi
if [ -f /usr/local/apps/msa-apigateway/deployment/env.git_sha ]; then
    . /usr/local/apps/msa-apigateway/deployment/env.git_sha
fi

sudo docker run -d \
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
    -e LOGBACK_CONFIG_PATH=/resources/logback-production.xml \
    -e TZ=Asia/Tokyo \
    --name msa-apigateway --rm \
    ghcr.io/extact-io/msa-apigateway:$GIT_SHA

exit $?