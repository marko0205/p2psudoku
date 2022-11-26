


docker build --no-cache -t p2psudoku .                                    
docker run -i --name MASTER-PEER -e MASTERIP="127.0.0.1" -e ID=0 p2psudoku
docker run -i --name peer-1 -e MASTERIP="127.0.0.1" -e ID=1 p2psudoku


docker network create --subnet=127.0.0.0/16 mysubnet && docker run -i --net mysubnet --ip 127.0.0.0 -e MASTERIP="127.0.0.2" -e ID=0 --name MASTER-PEER p2psudoku

docker run -i --net mysubnet -e MASTERIP="127.0.0.2" -e ID=1 --name PEER-1 p2psudoku

jj

## usefull commmand for debug 
docker build --no-cache -t p2psudoku .  

docker network create --subnet=172.20.0.0/16 customnetwork

docker run -i --net customnetwork --ip 172.20.0.10 -e MASTERIP="172.20.0.10" -e ID=0 --name MASTER-PEER p2psudoku

docker run -i --net customnetwork -e MASTERIP="172.20.0.10" -e ID=1 --name PEER-1 p2psudoku
docker run -i --net customnetwork -e MASTERIP="172.20.0.10" -e ID=2 --name PEER-2 p2psudoku
docker run -i --net customnetwork -e MASTERIP="172.20.0.10" -e ID=3 --name PEER-3 p2psudoku