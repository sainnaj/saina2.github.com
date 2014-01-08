/*Programer:JOHN SAINNA
 *Date:March, 29 2013
 *Other related files: Player.java, PlayerComparator.java
 *Description:This is a server program that represents a trivia game
 *It gets questions from a textfile and reads it to a client, reads the
 *answers back and then calculates/generates the answer and a feed back and sends it to
 *the client. Upto four players can play the game. The server prompts for a file and
 *then listens for clients. Each client is connected and given a separate thread. Threads communicate through
 *global variables

 */

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class FooServer {

    ServerSocket serverSocketMain = null;
    private static ArrayList<Player> listPlayer = new ArrayList<Player>();//to store txt
    private static ArrayList<String> list = new ArrayList<String>();//to store txt
    private static ArrayList<String> listName = new ArrayList<String>();//to store txt
    private static ArrayList<Integer> listScore = new ArrayList<Integer>();//to store txt
    private final static int numClients = 4;//Number of players
    private final static String congratulation = "Game Over!!!!!!!";
    private static int port = 8142;//Port number
    private static int begin = 0;//Track question number
    private static int numPlayers = 0;//
    private static int playerCounter = 0;
    private static volatile boolean isSorted = false;//For sorting
    //To track the state of players
    private static volatile boolean isDonep[] = {false,false,false,false};
    private static volatile boolean isPlayingp[] = {false,false,false,false};
    	//3456-8142


public FooServer()  {
         openFiles();//Read the file and populate the list array
         initArrayList();//Initialize ArrayLists
		//Listen for conection, if there is one, connect
		try {
		 serverSocketMain = new ServerSocket(port);
		 Thread thread1 = null;
		 Thread thread2 = null;
		 Thread thread3 = null;
		 Thread thread4 = null;

		 while(numPlayers < numClients){
		 	System.out.println("Waiting for players to join the game...");
		    Socket clientSocketMain  = serverSocketMain.accept();

		 	 if(numPlayers == 0){
		 	 	playerCounter++;
		 	 	NewServer1 sever1 = new NewServer1(clientSocketMain);
		 	 	thread1 = new Thread(sever1);
		 	 	setIsPlaying(0,true);
		 	 	thread1.start();
		 	 }

		 	 else if(numPlayers == 1){
		 	 	playerCounter++;
		 	 	NewServer2 sever2 = new NewServer2(clientSocketMain);
		 	 	thread2 = new Thread(sever2);
		 	 	setIsPlaying(1,true);
		 	 	thread2.start();
		 	 }

		 	 else if(numPlayers == 2){
		 	 	playerCounter++;
		 	 	NewServer3 sever3 = new NewServer3(clientSocketMain);
		 	 	thread3 = new Thread(sever3);
		 	 	setIsPlaying(2,true);
		 	 	thread3.start();

		 	 }
		 	 else{


		 	 	playerCounter++;
		 	 	NewServer4 sever4 = new NewServer4(clientSocketMain);
		 	 	thread4 = new Thread(sever4);
		 	 	setIsPlaying(3,true);
		 	 	thread4.start();
		 	 }
		 	 numPlayers++;
		 }	//End while


		   //Will spinlock untill both threads are not alive
		   //wait for all threads to finishe-spinlock
		   while(thread1.isAlive() || thread2.isAlive() || thread3.isAlive() || thread4.isAlive());
		   setIsSorted(false);//Make not sorted

		    try{
		    	 Thread.sleep(30000);
		    }catch(InterruptedException ex){
		    	ex.printStackTrace();
		    }

		}catch(IOException e) {
			System.out.println(e);
		}catch (NullPointerException e ){
			System.out.println(e);
		}finally{
			try{

			    serverSocketMain.close();
		    }catch(IOException ex){
			  ex.printStackTrace();
		    }
		}

}//End constructor





//Calculate the users answer based on the length of
//the string
public static String calculate(String str){
	int len;
	if(str.equals("0")){
		return "0";
	}
	len = str.length();
	len = len * 250;

	return (Integer.toString(len));

}
//Check to see if the wrong answer is in users string
public synchronized static boolean isRight(String userStr, String answer){
	if(userStr.indexOf(answer) < 0){
		return true;
	}
	return false;

	}
//Concatenate and return the right answer
public synchronized static String getAnswer(String ans){
	String str = "1234";
	String to = " ";
	for(int i = 0; i < 4; i++){
		if(str.charAt(i) == ans.charAt(0)){
		str = str.replace(ans.charAt(0),to.charAt(0));
		return str.trim();
		}
	}
	return "";
}
private void openFiles(){
	        String fileName;//The name of the file
	        fileName = JOptionPane.showInputDialog(null,"Enter a File name with a .txt extention");
		    File file = new File(fileName);
		    BufferedReader reader = null;

            String text = null;

	    	try {

	    	  reader = new BufferedReader(new FileReader(file));
    				 //Get the name of the file
            // repeat until all lines are read
            while ((text = reader.readLine()) != null) {
                list.add(text);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

}

public static synchronized void addName(String name){
	for(int i = 0; i < numClients; i++){

		if(listName.get(i) == null){
		  listName.add(i,name);
		  break;
		}
		else{

		}


	}//End for


}
public static synchronized void addScore(String name, int score){

	int index = listName.indexOf(name);
    listScore.remove(index);
	listScore.add(index,score);
}

public static synchronized  void sortPlayers(){
	setIsSorted(true);
	for(int i = 0; i < numClients; i++){

		if( listScore.get(i) != null){
	   		listPlayer.add(new Player(listName.get(i), listScore.get(i)));
		}
		else{

		}


	}
	PlayerComparator comparator = new PlayerComparator();
	Collections.sort(listPlayer, comparator);

}
//Initialize arrays
public ArrayList<String> initArrayList(){

		for(int i = 0; i < numClients && listScore.size() < numClients; i++){
			listScore.add(null);
			listName.add(null);

		}

	return listName;
}

//Get scores for winners
public static synchronized String getWinners(){
	String scores = "";
	for(int i = 0; i < listPlayer.size(); i++){
	   if(listPlayer.get(i) != null){
	   	 if(i == 0){
	   	 	scores += "First-" + (listPlayer.get(i).getName()) +" "+"score="+ (Integer.toString(listPlayer.get(i).getScore() )) + " ";
	   	 }
	   	 else if( i== 1){
	   	 	scores += "Second-" + (listPlayer.get(i).getName()) +" "+"score="+ (Integer.toString(listPlayer.get(i).getScore() )) + " ";

	   	 }
	   	 else if( i == 2 ){
	   	 	scores += "Third-" + (listPlayer.get(i).getName()) +" "+"score="+ (Integer.toString(listPlayer.get(i).getScore() )) + " ";

	   	 }
	   	 else{
	   	 	scores += "Fourth-" + (listPlayer.get(i).getName()) +" "+"score="+ (Integer.toString(listPlayer.get(i).getScore() )) + " ";

	   	 }

	   }

	}

  return scores;
}

//Get players status
private static synchronized boolean getIsDone(int index){
	return isDonep[index];

}
private static synchronized boolean setIsDone(int index, boolean bool){

	return isDonep[index] = bool;
}
private static synchronized boolean getIsPlaying(int index){
	return isPlayingp[index];

}
private static synchronized boolean setIsPlaying(int index, boolean bool){

	return isPlayingp[index] = bool;
}


private static synchronized void setIsSorted(boolean bool){

	isSorted = bool;
}
private static synchronized boolean getIsSorted(){

	return isSorted;
}


//Player #1
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
private class NewServer1 implements Runnable {
	    //Open the file and read from it
	    BufferedReader in;
	    PrintWriter out;
	    private Socket clientSocket1 = null;
	    private int total = 0;
	    private String totalInString;//Total in string form
		private String answer;//The right answer;
		private String answerClient;
		private String name;
		private int index = 0;
		String confirm = null;

	// constructor for player one
    NewServer1(Socket theClient) {
     this.clientSocket1 = theClient;

    }

    public void playTrivia(){

    	try {

    		out = new PrintWriter(clientSocket1.getOutputStream(),true);
		    in = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        	  try {

        	out.println("Enter your name");
            String playerName = in.readLine();
            addName(playerName);
            out.println(playerName);

             out.println(String.valueOf(begin));
             confirm = in.readLine();

        //Loop through the questions populated into an array list
        //add numbering and read them out to client.
        //Receive the answers back and generate the answers and feed back
        //and send it back to client
        int questionNum = 0;
        String question = "";
        for( ; begin < 10; begin++){/////////////////////////////////////////////////////////////////////////////
			for(int j = 0; j < 5; j++){

				if( j == 0){
					question = ""+ (begin+1) + "." +	list.get(index++).toString();//question
				}
				else{
					question = ""+ (j) + "." +	list.get(index++).toString();//Choices
				}

				out.println(question);	//Send it to client

        	}//End inner FOR

        		String userAnswer = in.readLine(); 	//Read the answer from client
        	answer = (list.get(index).toString());

        	if(answer.equals("0")){//if user answer was "0", they score a "0"
        		answerClient = "0";
        	}
        	else if(isRight(userAnswer, answer)){//check if it contains the wrong answer
        		answerClient = calculate(userAnswer);

        	}
        	else{
        		answerClient = "-250";//If it contains the wrong answer, the score is "-250"
        	}


        	total += Integer.parseInt(answerClient);//Total poits per question
        	totalInString = Integer.toString(total);//Total poits per question in string
        	answer = getAnswer(answer);
        	out.println(answer);
        	out.println(answerClient);
        	out.println(totalInString);
        	if(begin == 9){//If this is the last question

        	    addScore(playerName, total);

        	}


        	++index;
        	questionNum++;
        }//End For


                setIsDone(0,true);
        	    setIsPlaying(0,false);

        	//Wait for the rest of players to finish
        	while(getIsPlaying(1) && !getIsDone(1) ||  getIsPlaying(2) && !getIsDone(2) || getIsPlaying(3) && !getIsDone(3) );


             //Sort if not sorted
        	if( !getIsSorted()){
        		setIsSorted(true);
        		sortPlayers();
        	}

        	out.println(congratulation);
        	out.println(Integer.toString(playerCounter));
        	out.println(getWinners());

        	  }catch(IOException ex){
        	  	ex.printStackTrace();

        	  }
        	  //try {
        	  	//Close the streams and the sockets
	       	  	out.flush();
	       	  	//out.close();
        	  	//in.close();
        	  	//clientSocket1.close();
        	  	//serverSocket.close();
        	  //}catch(IOException ex){

        	  //	System.out.println(ex);

        	  //}


    }


// method below shows the thread’s run method called when started above
    public void run () {
    	playTrivia();
    	setIsDone(0,false);
    }//End run Method
}

private class NewServer2 implements Runnable {
	    //Open the file and read from it
	    BufferedReader in;
	    PrintWriter out;
	    private Socket clientSocket2 = null;
	    private int total;
	    private String totalInString;//Total in string form
		private String answer;//The right answer;
		private String answerClient;
        String confirm = null;

	// constructor for thread
    NewServer2(Socket theClient) {
      this.clientSocket2 = theClient;
    }

    public void playTrivia(){

    	try {

    		out = new PrintWriter(clientSocket2.getOutputStream(),true);
		    in = new BufferedReader(new InputStreamReader(clientSocket2.getInputStream()));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

        	out.println("Enter your name");
            String playerName = in.readLine();
            addName(playerName);
            out.println(playerName);

            out.println(String.valueOf(begin));
            confirm = in.readLine();

        //Loop through the questions populated into an array list
        //add numbering and read them out to client.
        //Receive the answers back and generate the answers and feed back
        //and send it back to client
        int questionNum = 0;
        String question = "";
        int index = begin * 6;//The present question in the list
        System.out.println("Index = " + index);
        for(int begin2 = begin; begin2 < 10; begin2++){
			for(int j = 0; j < 5; j++){

				if( j == 0){
					question = ""+ (begin2+1) + "." +	list.get(index++).toString();//question
				}
				else{
					question = ""+ (j) + "." +	list.get(index++).toString();//Choices
				}

				out.println(question);	//Send it to client

        	}//End inner FOR

        		String userAnswer = in.readLine(); 	//Read the answer from client
        	answer = (list.get(index).toString());

        	if(answer.equals("0")){//if user answer was "0", they score a "0"
        		answerClient = "0";
        	}
        	else if(isRight(userAnswer, answer)){//check if it contains the wrong answer
        		answerClient = calculate(userAnswer);

        	}
        	else{
        		answerClient = "-250";//If it contains the wrong answer, the score is "-250"
        	}


        	total += Integer.parseInt(answerClient);//Total poits per question
        	totalInString = Integer.toString(total);//Total poits per question in string
        	answer = getAnswer(answer);
        	out.println(answer);
        	out.println(answerClient);
        	out.println(totalInString);
        	if(begin2 == 9){//If this is the last question
        		addScore(playerName, total);

        	}
        	++index;
        	questionNum++;
        }//End For

                setIsDone(1,true);
        		setIsPlaying(1,false);

        //Wait for the other players to finish
        while(getIsPlaying(0) && !getIsDone(0) ||  getIsPlaying(2) && !getIsDone(2) || getIsPlaying(3) && !getIsDone(3) );

            if( !(getIsSorted())){
            	setIsSorted(true);
        		sortPlayers();
        	}
        	 out.println(congratulation);

        	out.println(Integer.toString(playerCounter));
        	out.println(getWinners());

        	  }catch(IOException ex){
        	  	ex.printStackTrace();


        	  }
        	  //
        	  	//Close the streams and the sockets
        	  	out.flush();



    }



   // method below shows the thread’s run method called when started above
    public void run () {

    	playTrivia();
    	setIsDone(1,false);

    }
}

//Player #3

private class NewServer3 implements Runnable {
	    //Open the file and read from it
	    BufferedReader in;
	    PrintWriter out;
	    private Socket clientSocket1 = null;
	    private int total = 0;
	    private String totalInString;//Total in string form
		private String answer;//The right answer;
		private String answerClient;
		private String name;
		String confirm = null;

	// constructor for thread
    NewServer3(Socket theClient) {
     this.clientSocket1 = theClient;

    }


    public void playTrivia(){

    	try {
    		out = new PrintWriter(clientSocket1.getOutputStream(),true);
		    in = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        	  try {

        	out.println("Enter your name");
            String playerName = in.readLine();
            addName(playerName);
            out.println(playerName);
            out.println(String.valueOf(begin));
            confirm = in.readLine();


        //Loop through the questions populated into an array list
        //add numbering and read them out to client.
        //Receive the answers back and generate the answers and feed back
        //and send it back to client
        int questionNum = 0;
        String question = "";
        int begin3 = begin;
        int index = begin * 6;
        for( ; begin3 < 10; begin3++){
			for(int j = 0; j < 5; j++){

				if( j == 0){
					question = ""+ (begin3+1) + "." +	list.get(index++).toString();//question
				}
				else{
					question = ""+ (j) + "." +	list.get(index++).toString();//Choices
				}

				out.println(question);	//Send it to client

        	}//End inner FOR

        	String userAnswer = in.readLine(); 	//Read the answer from client
        	answer = (list.get(index).toString());

        	if(answer.equals("0")){//if user answer was "0", they score a "0"
        		answerClient = "0";
        	}
        	else if(isRight(userAnswer, answer)){//check if it contains the wrong answer
        		answerClient = calculate(userAnswer);

        	}
        	else{
        		answerClient = "-250";//If it contains the wrong answer, the score is "-250"
        	}


        	total += Integer.parseInt(answerClient);//Total poits per question
        	totalInString = Integer.toString(total);//Total poits per question in string
        	answer = getAnswer(answer);
        	out.println(answer);
        	out.println(answerClient);
        	out.println(totalInString);
        	if(begin3 == 9){//If this is the last question
        	    addScore(playerName, total);
        	}

        	++index;
        	questionNum++;
        }//End For

                setIsDone(2,true);
        	    setIsPlaying(2,false);

        	while(getIsPlaying(0) && !getIsDone(0) ||  getIsPlaying(1) && !getIsDone(1) || getIsPlaying(3) && !getIsDone(3) );
        	if( !(getIsSorted())){
        		setIsSorted(true);
        		sortPlayers();
        	}
        	  out.println(congratulation);
        	  out.println(Integer.toString(playerCounter));
        	  out.println(getWinners());

        	  }catch(IOException ex){
        	  	ex.printStackTrace();


        	  }

        	  	//Close the streams and the sockets
	       	  	out.flush();



    }


// method below shows the thread’s run method called when started above
    public void run () {
    	playTrivia();
    	setIsDone(2,false);

    }//End run Method
}


//Player #4
private class NewServer4 implements Runnable {
	    //Open the file and read from it
	    BufferedReader in;
	    PrintWriter out;
	    private Socket clientSocket1 = null;
	    private int total = 0;
	    private String totalInString;//Total in string form
		private String answer;//The right answer;
		private String answerClient;
		private String name;
		String confirm = null;

	// constructor for thread
    NewServer4(Socket theClient) {
     this.clientSocket1 = theClient;

    }


    public void playTrivia(){

    	try {
    		out = new PrintWriter(clientSocket1.getOutputStream(),true);
		    in = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }

        	  try {

        	out.println("Enter your name");
            String playerName = in.readLine();
            addName(playerName);
            out.println(playerName);
            out.println(String.valueOf(begin));
            confirm = in.readLine();


        //Loop through the questions populated into an array list
        //add numbering and read them out to client.
        //Receive the answers back and generate the answers and feed back
        //and send it back to client
        int questionNum = 0;
        String question = "";
        int begin4 = begin;
        int index = begin * 6;
        for( ; begin4 < 10; begin4++){

			for(int j = 0; j < 5; j++){

				if( j == 0){
					question = ""+ (begin4+1) + "." +	list.get(index++).toString();//question
				}
				else{
					question = ""+ (j) + "." +	list.get(index++).toString();//Choices
				}

				out.println(question);	//Send it to client

        	}//End inner FOR

        		String userAnswer = in.readLine(); 	//Read the answer from client
        	    answer = (list.get(index).toString());

        	if(answer.equals("0")){//if user answer was "0", they score a "0"
        		answerClient = "0";
        	}
        	else if(isRight(userAnswer, answer)){//check if it contains the wrong answer
        		answerClient = calculate(userAnswer);

        	}
        	else{
        		answerClient = "-250";//If it contains the wrong answer, the score is "-250"
        	}


        	total += Integer.parseInt(answerClient);//Total poits per question
        	totalInString = Integer.toString(total);//Total poits per question in string
        	answer = getAnswer(answer);
        	out.println(answer);
        	out.println(answerClient);
        	out.println(totalInString);

        	if(begin4 == 9){//If this is the last question

        	    addScore(playerName, total);

        	}

        	++index;
        	questionNum++;
        }//End For



                setIsDone(3,true);	//At this point player1 is done playing
        	    setIsPlaying(3,false);


        	//Wait for the rest of players
        	while(getIsPlaying(0) && !getIsDone(0) ||  getIsPlaying(1) && !getIsDone(1) || getIsPlaying(2) && !getIsDone(2) );

        	if( !(getIsSorted())){
        		setIsSorted(true);
        		sortPlayers();
        	}
        	out.println(congratulation);
        	out.println(Integer.toString(playerCounter));
        	out.println(getWinners());

        	  }catch(IOException ex){
        	  	ex.printStackTrace();


        	  }

        	  	//Close the streams and the sockets
	       	  	out.flush();

    }


// method below shows the thread’s run method called when started above
    public void run () {

    	playTrivia();
    	setIsDone(3,false);
    	System.out.println("Finish void run player #4 ");


    }//End run Method
}

//Main method
public static void main(String[] args)  {
	FooServer d2 = new FooServer();

}

}//End Class