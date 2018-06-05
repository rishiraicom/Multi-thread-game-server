To execute the program:

1.)complie the program using the command: javac *.java
2.)execute the sever using the command: java MultiThreadGameServer
3.)to make a new clent open a new terminal and type command: java MultiThreadGameClient

Our program provides a list of commands that can be executed at any time.
The command list include:
1.request [name of online player] :to request an online player
2.listonline :to get the updated list of player online
3.dropreq :to drop an already made request
4.reqlist :to diaplay names of people who made request to you 
5.accept [name of the person who made a request to you]:to accept reques
6.--quit :to leave the game server


In our program deadlock will never occur as we give the player with an option to drop their request.

 