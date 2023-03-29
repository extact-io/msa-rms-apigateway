#!/bin/sh
LOGBACK_CONFIG_OPT=""
if [ -n "$LOGBACK_CONFIG_PATH" ]; then
  LOGBACK_CONFIG_OPT="-Dlogback.configurationFile="${LOGBACK_CONFIG_PATH}
fi

java ${LOGBACK_CONFIG_OPT} -jar $1
