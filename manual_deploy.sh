#!/bin/bash

if [ -e ./deployment ]; then
  rm -rf ./target/deployment
fi

GIT_SHA=`git show --format='%H' --no-patch`
mvn -Pcli,copy-libs clean package docker:build docker:push -Dimage.tag=$GIT_SHA -DskipTests=true

mkdir ./target/deployment
cp ./env/deployment/appspec.yml ./target/deployment
cp ./env/deployment/logback-production.xml ./target/deployment
cp -r ./env/deployment/scripts ./target/deployment
echo $GIT_SHA > ./target/deployment/image_tag

ZIP_NAME="deployment/msa-apigateway-"`date "+%Y%m%d_%H%M%S"`.zip

aws deploy push \
  --application-name msa-apigateway \
  --s3-location s3://rms-codedeploy-bucket/$ZIP_NAME \
  --ignore-hidden-files \
  --source ./target/deployment
aws deploy create-deployment \
  --application-name msa-apigateway \
  --deployment-config-name CodeDeployDefault.OneAtATime \
  --deployment-group-name msa-apigateway-deploy-group \
  --s3-location bucket=rms-codedeploy-bucket,bundleType=zip,key=$ZIP_NAME
