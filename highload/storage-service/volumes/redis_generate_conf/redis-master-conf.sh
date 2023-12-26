echo "requirepass ${REDIS_PASSWORD}" > "${REDIS_CONF_PATH}"
echo "port ${REDIS_MASTER_PORT}" >> "${REDIS_CONF_PATH}"
redis-server "${REDIS_CONF_PATH}"