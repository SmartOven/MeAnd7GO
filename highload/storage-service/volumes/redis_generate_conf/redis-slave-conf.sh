echo "requirepass ${REDIS_PASSWORD}" > "${REDIS_CONF_PATH}"
{
echo "port ${REDIS_SLAVE_PORT}"
echo "replicaof ${REDIS_MASTER_IP} ${REDIS_MASTER_PORT}"
echo "masterauth ${REDIS_MASTER_PASSWORD}"
echo "slave-read-only yes"
} >> "${REDIS_CONF_PATH}"
redis-server "${REDIS_CONF_PATH}"
