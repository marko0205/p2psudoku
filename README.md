# P2P - Sudoku
#### University project for Architecture Distributed for the Cloud

## Project Description
Design and develop a Sudoku challenge game on a P2P network. Each user can place a number of the sudoku game; if it is not already placed takes 1 point, if it is already placed and it is rights take 0 points, in other case receive -1 point. The games are based on a 9 x 9 matrix. All users that play to a game are automatically informed when a user increment its score and when the game is finished. The system allows the users to generate (automatically) a new Sudoku challenge identified by a name, join in a challenge using a nickname, get the integer matrix describing the Sudoku challenge, and place a solution number.
<p align="left"><img src="Assets/sudoku.png" width="300" style="border: 6px solid black" /></p>

## Features

- Generate random Sudoku challenge
- Join a challenge 
- Multiplayer (tested up to 8 players on a single challenge)
- Show Scoreboard
- Leave a challenge
- Auto-remove completed challenges

## API description

The interface SudokuGameImpInterface contains the following methods:

- **generateNewSudoku**: allows the peers to generate & join in a new challenge 
- **syncGameList**: commit the list of active challenges into the DHT 
- **addToPlayerList**: allow the peers to login, choosing a nickname
- **findPlayer**: check if a nickname is available
- **join**: allow peers to join an active challenge
- **searchGame**: given a name return an available challenge
- **placeNumber**: allows peers in a challenge to put a value on the sudoku
- **downloadGameList**: get the list of active challenges from the DHT 
- **updatePlayerList**: get the list of logged players 
- **leaveNetwork**: allows peers to announce the shutdown and logout from the game
- **leaveSudoku**: allows peers to leave an active or terminated challenge

## Problem solution 
After the startup the user will see a welcome message and will be asked to input a nickname for access to the menu. 

Creating a new sudoku, will insert into the DHT a new Sudoku instance (containing all the data about the game and the users scores).
Joining and put values into a sudoku challenge means retrieve the sudoku instance from the DHT, update the state of the object and put it back into the DHT.
The main client (StaticClient) do not support the auto-refresh, so the user is responsible to refresh his sudoku instance, in order to retrive the last version from the DHT (auto-refresh mode is still in develop... look the future developments).

Once a Sudoku is completed, the user can see the Score Board and once all the players left the challenge this will be removed from the DHT.

## Tech 

This project uses a number of open source projects to work properly:

- [Java] - programming language
- [TomP2P] - Library to create the DHT
- [Maven] - Project management used to develop
- [JUnit-5] - Framework used for testing 
- [Docker] - Software used to manage containers
- [args4j] - Java library to parse command line options/arguments
- [Text-IO] - library for creating Java console applications

## Building 
This project is managed by Maven, the application can be built by running mvn package

A Dockerfile is also provided to build a container that performs packaging of the application. To build such container use the command:

```sh
docker build --no-cache -t p2psudoku . 
```

This command must be used in the folder where this repository has been cloned.

## Run

The application is packaged in a JAR that can be executed though this command from the project root directory

```sh
java -jar target/p2psudoku-1.0-SNAPSHOT-jar-with-dependencies.jar
```
and passing two arguments: **-m** is the master IP address and **-id** is the peer's unique identifier

To execute in a docker eviroment after create the container, you shuld define a subnet, in order to make the containers communicate

```sh
docker network create --subnet=172.20.0.0/16 customnetwork
```

after that you can run the master node with the following command

```sh
docker run -i --net customnetwork --ip 172.20.0.10 -e MASTERIP="172.20.0.10" -e ID=0 --name MASTER-PEER p2psudoku
```

and then all the other peers:

```sh
docker run -i --net customnetwork -e MASTERIP="172.20.0.10" -e ID=X --name PEER-X p2psudoku
```


## Future developments

The class Client is an alternative client which support auto-refresh and implement most of the methods of the interface described before. Once a player join into a challenge, for each update of the Sudoku instance, a message will be send to all the interested peers. Once received, the thread resposable of showing the sudoku board and wait for user input, will be destroyed and restarted with the updated state (passed by the message). 
Once the sudoku is completed the users will automatically see the score board and after a few seconds will be redirected to the main menu. 
All this work till the end of the first game, but once a thread join into another challenge, the threads start behaving abnormally. 

One of the main problems is that the terminal read operation is blocking and so sometime the Thread.interrupt() fail. Anyway i still didn't find a work around

For these reasons, the static version has been released

## License

MIT

**Free Software, Hell Yeah!**

   [Java]: <https://docs.oracle.com/en/java/>
   [TomP2P]: <https://github.com/tomp2p/TomP2P>
   [Maven]: <https://github.com/apache/maven>
   [Junit-5]: <https://github.com/junit-team/junit5>
   [Docker]: <https://github.com/docker>
   [args4j]: <https://github.com/kohsuke/args4j>
   [Text-IO]: <https://github.com/beryx/text-io>
  
