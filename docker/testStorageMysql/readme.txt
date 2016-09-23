First of all install docker. https://docs.docker.com/engine/installation/
Make sure that it works. $ docker --help

Then you need to build image with mysql.
To build image run: $ docker build -t yourtag .
Then run '$ docker images'. Your image should appear in the list.

To start container run start.sh script.
If it's first time you running this script it'll create a new container and start it.
If container was already created, it'll just start it or do nothing if it's already running.
start.sh script can be edited if you want to change some parameters (look into docker-entrypoint.sh to see all parameters used.
docker-entrypoint.sh is script which is used by container to run mysql. When you run '$ docker run mysql' this script's being executed).

After that you can run any mysql client and connect to the db with credentials specified in the start.sh.

To stop docker container run: $ docker stop your_container_name
To remove docker container run: $ docker rm your_container_name