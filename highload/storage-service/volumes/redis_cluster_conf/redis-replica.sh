echo "requirepass ${REDIS_PASSWORD}" > "${REDIS_CONF_PATH}"
{
echo "port ${REDIS_REPLICA_PORT}"
echo "masterauth ${REDIS_MASTER_PASSWORD}"
echo "cluster-enabled ${REDIS_CLUSTER_ENABLED:no}"
echo "slave-read-only yes"
} >> "${REDIS_CONF_PATH}"
redis-server "${REDIS_CONF_PATH}"
