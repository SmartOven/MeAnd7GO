echo "requirepass ${REDIS_PASSWORD}" > "${REDIS_CONF_PATH}"
{
echo "port ${REDIS_REPLICA_PORT}"
#echo "slaveof ${REDIS_MASTER_IP} ${REDIS_MASTER_PORT}"
echo "masterauth ${REDIS_MASTER_PASSWORD}"
echo "cluster-enabled ${REDIS_CLUSTER_ENABLED:no}"
} >> "${REDIS_CONF_PATH}"
redis-server "${REDIS_CONF_PATH}"
