#!/bin/bash
# - env/deployment/scripts/docker_stop.shのデバック用スクリプト
# - 内容はdocker-compose.ymlと同じなので普段はdocker-composeを使う方が便利

END_STATUS=0

docker stop msa-apigateway || {
    	echo "msa-apigateway stop fail error:$?" && END_STATUS=1
    }
docker stop msa-service-item || {
    	echo "msa-service-item stop fail error:$?" && END_STATUS=1
    }
docker stop msa-service-reservation || {
    	echo "msa-service-reservation stop fail error:$?" && END_STATUS=1
    }
docker stop msa-service-user || {
    	echo "msa-service-user stop fail error:$?" && END_STATUS=1
    }
docker stop jaeger || {
    	echo "jaeger stop fail error:$?" && END_STATUS=1
    }
docker network rm msa-apigateway-network  || {
    	echo "msa-apigateway-network remove fail error:$?" && END_STATUS=1
    }

exit $END_STATUS 
