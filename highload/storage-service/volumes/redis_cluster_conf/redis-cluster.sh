{
echo "cluster-require-full-coverage no"
echo "cluster-node-timeout 15000"
echo "cluster-config-file nodes.conf"
echo "cluster-migration-barrier 1"
echo "appendonly yes"
echo "cluster-enabled ${REDIS_CLUSTER_ENABLED}"
echo "port ${REDIS_CLUSTER_PORT}"
echo "requirepass ${REDIS_PASSWORD}"
} > "${REDIS_CONF_PATH}"

redis-cli --cluster create "${REDIS_MASTER_A_IP}" "${REDIS_MASTER_B_IP}" "${REDIS_MASTER_C_IP}" --cluster-yes -a "${REDIS_PASSWORD}"

redis-cli --cluster add-node "${REDIS_SLAVE_A_IP}" "${REDIS_MASTER_A_IP}" --cluster-slave -a "${REDIS_PASSWORD}"
redis-cli --cluster add-node "${REDIS_SLAVE_B_IP}" "${REDIS_MASTER_B_IP}" --cluster-slave -a "${REDIS_PASSWORD}"
redis-cli --cluster add-node "${REDIS_SLAVE_C_IP}" "${REDIS_MASTER_C_IP}" --cluster-slave -a "${REDIS_PASSWORD}"