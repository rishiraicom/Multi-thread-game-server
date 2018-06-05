import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

public class MultiThreadGameServer {


  private static ServerSocket serverSocket = null;
  private static Socket clientSocket = null;
  private static final int maxClientsCount = 100;
  private static final clientThread[] threads = new clientThread[maxClientsCount];

  public static void main(String args[]) {

    
    int portNumber = 8888;
    System.out.println("Now using port number=" + portNumber);
    
    try {
      serverSocket = new ServerSocket(portNumber);
    } catch (IOException e) {
      System.out.println(e);
    }

    while (true) {
      try {
        clientSocket = serverSocket.accept();
        int i = 0;
        for (i = 0; i < maxClientsCount; i++) {
          if (threads[i] == null) {
            (threads[i] = new clientThread(clientSocket, threads)).start();
            break;
          }
        }
        if (i == maxClientsCount) {
          PrintStream os = new PrintStream(clientSocket.getOutputStream());
          os.println("Server too busy. Try later.");
          os.close();
          clientSocket.close();
        }
      } catch (IOException e) {
        System.out.println(e);
      }
    }
  }
}


class clientThread extends Thread {

  String clientName = null;
  String opponentName = null;
  String requestedopponentName = null;
  Boolean requestsent = false;
  Boolean paired = false;
  private DataInputStream is = null;
  private PrintStream os = null;
  private Socket clientSocket = null;
  private final clientThread[] threads;
  int maxClientsCount;

  public clientThread(Socket clientSocket, clientThread[] threads) {
    this.clientSocket = clientSocket;
    this.threads = threads;
    maxClientsCount = threads.length;
  }

  public void run() {
    int maxClientsCount = this.maxClientsCount;
    clientThread[] threads = this.threads;

    try {
      
      is = new DataInputStream(clientSocket.getInputStream());
      os = new PrintStream(clientSocket.getOutputStream());
      String name;
      os.println("Enter your name.");
      name = is.readLine().trim();
 
      os.println("\n\nWelcome " + name
          + " to the game server.\nTo find the list of all online player type: listonline\nTo leave enter /quit in a new line.\nTo see the list of all commands type: listcom");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i] == this) {
            clientName = name;
            break;
          }
        }
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] != null && threads[i].paired != true && threads[i] != this ) {
            int check_pl = 0;
			threads[i].os.println("\nA new player " + name
                + " has entered the game server");
			        threads[i].os.println("Updated list of people online and redy to play:");
			        for (int i_in = 0; i_in < maxClientsCount; i_in++){
				        if (threads[i_in] != null && threads[i_in].paired != true){
					        threads[i].os.println(threads[i_in].clientName); //display list of all online players
				        }
			        }
					threads[i].os.println("list of people currently playing:");
					for (int i_in = 0; i_in < maxClientsCount; i_in++){
				        if (threads[i_in] != null && threads[i_in].paired == true){
					        threads[i].os.println(threads[i_in].clientName);
				            check_pl = 1;
						}
			        }
					if (check_pl != 1){
						threads[i].os.println("no one currently playing ");
					}
		    }
		}
          
        
      }
	  //main while loop
      while (true) {
		  
        
		if (this.paired == true){
			synchronized (this) {
				for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(this.opponentName)) {
						  this.os.println("game running between you and " + this.opponentName);
						  this.os.println("type quit to quit this game");
						  
						  String line = is.readLine();
						  while(true){  
	                          				  
							  
							  if (!(line.equals("quit")) && threads[i].paired != false){
								  threads[i].os.println(name+":"+line);
								  line = is.readLine();
								  
							  }else 
								  break;
						  }
						  if(this.paired == false)
						        break;
						  this.os.println("game ended");
						  threads[i].os.println("game ended, type anythong and press enter to continue");
						  this.paired = false;
						  threads[i].paired = false;
						  this.requestsent = false;
						  threads[i].requestsent = false;
						  
						  break;
						  
                  }
                }
				
		}}
       
		String line = is.readLine();
        if (line.startsWith("--quit")) {
			synchronized (this) {
			for (int i = 0; i < maxClientsCount; i++){
				if (threads[i] != null && threads[i].requestsent == true && threads[i].requestedopponentName.equals(name)){
					        threads[i].requestsent = false;
							threads[i].requestedopponentName = null;
					        threads[i].os.println("player "+ name +", who you have made request to has left");
							threads[i].os.println(" please send request to some other online player\n");
				        }
			        }
			int check_pl = 0;
			for (int i = 0; i < maxClientsCount; i++) {
                if (threads[i] != null && threads[i].paired != true) {
                    threads[i].os.println("user " + name + " left the game server");					
			        threads[i].os.println("Updated list of people online and redy to play:"+"\n");
			        for (int i_in = 0; i_in < maxClientsCount; i_in++){
				        if (threads[i_in] != null && threads[i_in] != this && threads[i_in].paired != true){
					        threads[i].os.println(threads[i_in].clientName); //display list of all online players
				        }
			        }
					threads[i].os.println("\n");
					threads[i].os.println("new list of people currently playing:"+"\n");
					for (int i_in = 0; i_in < maxClientsCount; i_in++){
				        if (threads[i_in] != null && threads[i_in].paired == true){
					        threads[i].os.println(threads[i_in].clientName);
							check_pl = 1;
				        }
			        }
					if (check_pl != 1){
						threads[i].os.println("no one currently playing ");
					}
					
		        }
		    }
			}
			
            break;
			
        } else
		if (line.startsWith("reqlist")) {
              synchronized (this) {
				this.os.println("list of people who made request to you:"); 
		        int check=0;
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].paired != true
					  && threads[i].requestedopponentName.equals(name)) {                    
                    this.os.println(threads[i].clientName);
					check=1;               
                  }
                }
				if (check==0){
					this.os.println("sorry, noone has made a request to you"); 
				}
              }
            
          
        }
		else
        if (line.startsWith("request")) {
		if (this.requestsent == true){
			this.os.println("you have pending request to "+ this.requestedopponentName);
			this.os.println("please withdraw this request before requesting another person");
			continue;
		}
          String[] words = line.split("\\s", 2);
          if (words.length > 1 && words[1] != null) {
            words[1] = words[1].trim();
            if (!words[1].isEmpty()) {
              synchronized (this) {
		        int pop =0;
                for (int i = 0; i < maxClientsCount; i++) {
                  if (threads[i] != null && threads[i] != this
                      && threads[i].clientName != null
                      && threads[i].clientName.equals(words[1])
					  && threads[i].requestsent == false) {
                    threads[i].os.println(name + " " + "has requested to play with you");
					threads[i].os.println("to accept this request type:accept "+name+"\n");
                    this.requestedopponentName = words[1];
			        this.requestsent = true;                
                    this.os.println("request made to " + words[1]);
					pop=1;
                    break;
                  }
				  
                }
				if (pop==0){
					this.os.println("You enterd wrong name or the player is no longer ready to play.");
				}
				continue;
              }
            }
          }
		  
        } else if (line.startsWith("accept")) {
			String[] words = line.split("\\s", 2);
            if (words.length > 1 && words[1] != null) {
                words[1] = words[1].trim();
                if (!words[1].isEmpty()) {
                    synchronized (this) {        
		            int is_done = 0;
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null
                            && threads[i].clientName.equals(words[1])
					        && threads[i].requestsent == true
							&& threads[i].requestedopponentName.equals(name)) {
                                threads[i].os.println(name + " has accpted your request");
								threads[i].os.println("type anything and press enter to continue");
					            threads[i].opponentName = name;
								threads[i].paired = true;
								
								this.paired = true;
								this.opponentName = words[1]; 
                                this.os.println("connection with " + words[1] + " established");
                                is_done=1;
								
								for (int i_in = 0; i_in < maxClientsCount; i_in++) {
						
                                    if (threads[i_in] != null && threads[i_in] != this
                                        && threads[i_in].clientName != null
							            && threads[i_in].paired != true 
					                    && threads[i_in].requestsent == true
							            && threads[i_in].requestedopponentName.equals(threads[i].clientName)) {
                                            threads[i_in].os.println(threads[i].clientName + " has started playing with someone else");
					                        threads[i_in].requestsent = false;
								            threads[i_in].requestedopponentName = null;
                           
                        }
                    }
					            break;
                        }
                    }
					
					for (int i = 0; i < maxClientsCount; i++) {
						if(is_done == 1)
                        if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null
							&& threads[i].paired != true 
					        && threads[i].requestsent == true
							&& threads[i].requestedopponentName.equals(name)) {
                                threads[i].os.println(name + " has accpted someone else request");
					            threads[i].requestsent = false;
								threads[i].requestedopponentName = null; 
                           
                        }
                    }
					if(is_done == 0){
						this.os.println("wrong name");
					}
                    }
                }
            }
        }else if (line.startsWith("dropreq")) {
		            if (this.requestsent==false){
						this.os.println("you have made no requestsor the person you resuested is now busy or left the server");
						this.os.println("This command cannot be executed");
						break;
						
					}
                    synchronized (this) {        
                    for (int i = 0; i < maxClientsCount; i++) {
                        if (threads[i] != null && threads[i] != this
                            && threads[i].clientName != null
                            && threads[i].clientName.equals(this.requestedopponentName)) {
                                threads[i].os.println( name + " has cancelled their request");
								threads[i].os.println( "To see the updated list of people who have made request to you type: reqlist ");
								this.requestsent=false; 
								this.requestedopponentName=null;
                                this.os.println("you have pulled your request");
								this.os.println("to make a request again use the request command");
                           
                        }
                    }
                    }
                }else 
					if (line.equals("listonline")){
						synchronized (this) {
        
			                this.os.println("Updated list of people online and redy to play:");
			                for (int i_in = 0; i_in < maxClientsCount; i_in++){
				                if (threads[i_in] != null && threads[i_in].paired != true){
					                this.os.println(threads[i_in].clientName); //display list of all online players
				                }
			                }
					
		    }
		}else 
					if (line.equals("listcom")){
						synchronized (this) {
        
			            this.os.println("\nAvilable commands:\n");
			            this.os.println("1.request [name of online player] :to request an online player");
                        this.os.println("2.listonline :to get the updated list of player online");
                        this.os.println("3.dropreq :to drop an already made request");
						this.os.println("4.reqlist :to diaplay names of people who made request to you ");
                        this.os.println("5.accept [name of the person who made a request to you]:to accept request ");						
					    
		    }
		}else{
			this.os.println("Unknown command");
		}
          
        
      }
					
      this.os.println("end_this");
      synchronized (this) {
        for (int i = 0; i < maxClientsCount; i++) {
          if (threads[i] == this) {
            threads[i] = null;
          }
        }
      }

      is.close();
      os.close();
      clientSocket.close();
	
    } catch (IOException e) {
    }
  }
}