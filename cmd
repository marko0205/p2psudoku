


docker build --no-cache -t p2psudoku .                                    
docker run -i --name MASTER-PEER -e MASTERIP="127.0.0.1" -e ID=0 p2psudoku
docker run -i --name peer-1 -e MASTERIP="172.17.0.2" -e ID=1 p2psudoku