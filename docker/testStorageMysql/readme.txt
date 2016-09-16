First of all install docker. https://docs.docker.com/engine/installation/
Make sure that it works. $ docker --help

Then you need to build image with mysql.
To build image run: $ docker build -t yourtag .
Then run '$ docker images'. Your image should appear in the list.

To start container run start.sh script. 
It can be edited if you want to change some parameters (look into docker-entrypoint.sh to see all parameters used.
docker-entrypoint.sh is script which is used by container to run mysql. When you run '$ docker run mysql' this script's being executed).

After that you can run any mysql client and connect to the db with credentials specified in the start.sh.
