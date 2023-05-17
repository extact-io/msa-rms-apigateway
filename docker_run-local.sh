#!/bin/bash
# - env/deployment/scripts/docker_run.shのデバック用スクリプト
# - 内容はdocker-compose.ymlと同じなので普段はdocker-composeを使う方が便利

END_STATUS=0

# Create docker network
docker network create -d bridge msa-apigateway-network  || {
    	echo "msa-apigateway-network create fail error:$?" && END_STATUS=1
    }

# Start msa-apigateway
docker run -d \
    -p 7001:7001 \
    -p 7011:7011 \
    -e ENV_RMS_SERVICE_URL_ITEM=http://msa-service-item:7002 \
    -e ENV_RMS_SERVICE_URL_RESERVATION=http://msa-service-reservation:7003 \
    -e ENV_RMS_SERVICE_URL_USER=http://msa-service-user:7004 \
    -e TZ=Asia/Tokyo \
    -e TRACING_ENABLED=true \
    -e TRACING_HOST=jaeger \
    --name msa-apigateway --rm \
    --network msa-apigateway-network \
    extact-io/msa-apigateway:latest || {
    	echo "msa-apigateway start fail error:$?" && END_STATUS=1
    }

# Start msa-service-item
docker run -d \
    -e TZ=Asia/Tokyo \
    -e TRACING_ENABLED=true \
    -e TRACING_HOST=jaeger \
    --name msa-service-item --rm \
    --network msa-apigateway-network \
    extact-io/msa-service-item:latest || {
    	echo "msa-service-item start fail error:$?" && END_STATUS=1  
    }

# Start msa-service-reservation
docker run -d \
    -e ENV_RMS_SERVICE_URL_ITEM=http://msa-service-item:7002 \
    -e ENV_RMS_SERVICE_URL_USER=http://msa-service-user:7004 \
    -e TZ=Asia/Tokyo \
    -e TRACING_ENABLED=true \
    -e TRACING_HOST=jaeger \
    --name msa-service-reservation --rm \
    --network msa-apigateway-network \
    extact-io/msa-service-reservation:latest || {
    	echo "msa-service-reservation start fail error:$?" && END_STATUS=1  
    }

# Start msa-service-user
docker run -d \
    -e TZ=Asia/Tokyo \
    -e TRACING_ENABLED=true \
    -e TRACING_HOST=jaeger \
    --name msa-service-user --rm \
    --network msa-apigateway-network \
    extact-io/msa-service-user1:latest || {
    	echo "msa-service-user start fail error:$?" && END_STATUS=1  
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
