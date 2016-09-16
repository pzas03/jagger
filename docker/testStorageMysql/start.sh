MYSQL_USER="jaggeruser"
MYSQL_DATABASE="jaggerdb"
MYSQL_CONTAINER_NAME="jagger_mysql"

MYSQL_ROOT_PASSWORD="root"
MYSQL_PASSWORD="password"
MYSQL_EXTERNAL_PORT=3306
MYSQL_CONTAINER_PORT=3306

#MYSQL_ROOT_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)
#MYSQL_PASSWORD=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | sed 1q)

docker \
  run \
  --detach \
  --env MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD} \
  --env MYSQL_USER=${MYSQL_USER} \
  --env MYSQL_PASSWORD=${MYSQL_PASSWORD} \
  --env MYSQL_DATABASE=${MYSQL_DATABASE} \
  --name ${MYSQL_CONTAINER_NAME} \
  --publish $MYSQL_EXTERNAL_PORT:$MYSQL_CONTAINER_PORT \
  mysql:5.7;
