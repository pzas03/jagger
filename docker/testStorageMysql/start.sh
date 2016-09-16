MYSQL_USER="jaggeruser"
MYSQL_DATABASE="jaggerdb"
MYSQL_CONTAINER_NAME="jagger_mysql"

MYSQL_ROOT_PASSWORD="root"
MYSQL_PASSWORD="password"
MYSQL_EXTERNAL_PORT=3307
MYSQL_CONTAINER_PORT=3306

#MYSQL_ROOT_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)
#MYSQL_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)


is_container_created=$(docker ps -a | grep ${MYSQL_CONTAINER_NAME})
is_container_running=$(docker ps | grep ${MYSQL_CONTAINER_NAME})

if [ -z "${is_container_created}" ]; then
    echo "Container ${MYSQL_CONTAINER_NAME} is not created. Creating..."
    docker \
      run \
      --detach \
      --env MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
      --env MYSQL_USER=${MYSQL_USER} \
      --env MYSQL_PASSWORD=${MYSQL_PASSWORD} \
      --env MYSQL_DATABASE=${MYSQL_DATABASE} \
      --name ${MYSQL_CONTAINER_NAME} \
      --publish ${MYSQL_EXTERNAL_PORT}:${MYSQL_CONTAINER_PORT} \
      mysql:5.7;
elif [ ! -z "${is_container_running}" ]; then
    echo "Container ${MYSQL_CONTAINER_NAME} is already started and running. (try '$ docker ps')"
else
    echo "Container ${MYSQL_CONTAINER_NAME} is already created. Starting..."
    docker start ${MYSQL_CONTAINER_NAME}
fi

