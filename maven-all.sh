#!/bin/bash
#-----------------------
# define command
#-----------------------
INSTALL_CMD="mvn -Pcli clean install -DskipTests="
PACKAGE_CMD="mvn -Pcli,copy-libs clean package -DskipTests="
IMAGE_CMD="mvn -Pcli,copy-libs clean package docker:build -DskipTests="

EXEC_CASE="package"
if [ $# != 0 ]; then
  EXEC_CASE=$1
fi

SKIP_TEST="true"
if [ $# = 2 ]; then
  SKIP_TEST=$2
fi

#-----------------------
# define directory
#-----------------------
POM_DIR="msa-rms-parent"
PLATFORM_DIR="msa-rms-platform"
GW_DIR="msa-rms-apigateway"
ITEM_DIR="msa-rms-service-item"
RSV_DIR="msa-rms-service-reservation"
USER_DIR="msa-rms-service-user"

#-----------------------
# define function
#-----------------------
execute_cmd() {
  eval ${1}
  if [ $? -ne 0 ]; then
    exit 1
  fi
}

install() {
  CMD=$INSTALL_CMD$1
  cd "../${POM_DIR}"
  execute_cmd "${CMD}"
  cd "../${PLATFORM_DIR}"
  execute_cmd "${CMD}"
}
install-all() {
  install $1
  CMD=$INSTALL_CMD$1
  cd "../${GW_DIR}"
  execute_cmd "${CMD}"
  cd "../${ITEM_DIR}"
  execute_cmd "${CMD}"
  cd "../${RSV_DIR}"
  execute_cmd "${CMD}"
  cd "../${USER_DIR}"
  execute_cmd "${CMD}"
}

package() {
  CMD=$PACKAGE_CMD$1
  cd "../${GW_DIR}"
  execute_cmd "${CMD}"
  cd "../${ITEM_DIR}"
  execute_cmd "${CMD}"
  cd "../${RSV_DIR}"
  execute_cmd "${CMD}"
  cd "../${USER_DIR}"
  execute_cmd "${CMD}"
}

install-package() {
  install $1
  package $1
}

image() {
  CMD=$IMAGE_CMD$1
  cd "../${GW_DIR}"
  execute_cmd "${CMD}"
  cd "../${ITEM_DIR}"
  execute_cmd "${CMD}"
  cd "../${RSV_DIR}"
  execute_cmd "${CMD}"
  cd "../${USER_DIR}"
  execute_cmd "${CMD}"
}

install-image() {
  install $1
  image $1
}

#-----------------------
# execute cases
#-----------------------
echo "================================="
echo "cmd => ${EXEC_CASE}:skipTests=${SKIP_TEST}"
echo "================================="
case "${EXEC_CASE}" in
  "install")
    install $SKIP_TEST
    ;;
  "install-all")
    install-all $SKIP_TEST
    ;;
  "package")
    package $SKIP_TEST
    ;;
  "package-all")
    install-package $SKIP_TEST
    ;;
  "image")
    image $SKIP_TEST
    ;;
  "image-all")
    install-image $SKIP_TEST
    ;;
  *)
    package $SKIP_TEST
    ;;
esac

exit
