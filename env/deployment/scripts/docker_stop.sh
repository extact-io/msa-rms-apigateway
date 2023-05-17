#!/bin/bash

echo "[msa-apigateway] STOP..."

END_STATUS=0

docker stop msa-apigateway || {
    	echo "msa-apigateway stop fail error:$?" && END_STATUS=1
    }
docker stop jaeger || {
    	echo "jaeger stop fail error:$?" && END_STATUS=1
    }
docker network rm msa-apigateway-network  || {
    	echo "msa-apigateway-network remove fail error:$?" && END_STATUS=1
    }

exit $END_STATUS 
