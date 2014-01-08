/**
**Programmer: John Sainna
*Program Name FooClient.java
*Main Program Name FooServe.java:
*Programming Language: Java
*Date: March,29 2013
*Program Description: This is a client part
*of the program for playing a trivia game.
*

**/
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FooClient extends JFrame {

	private Socket toServerSocket;
	private BufferedReader inClient;
	private PrintWriter outClient;
    private JLabel lbl2, lbl3,lbl4,lbl5,lbl6,lbl7,lbl8,lbl9;
    private JTextField jtext2,jtext3,jtext4,jtext5,jtext6;
    private JTextArea jTextArea,jTextArea2;
    private JPanel panel2,panel3,panel4,panel5,panel6;
	private static String total = "";
	private static String userInput;
	private boolean isDone = false;
	private final static int port = 8142;
	private static int index = 0;
	private static JOptionPane optionPane = new JOptionPane();


public FooClient() {
	try {

	  System.out.println("CLIENT IS ATTEMPTING CONNECTION...");
	  toServerSocket = new Socket("localhost", port);
	  System.out.println("CONNECTION HAS BEEN MADE");

	  inClient  = new BufferedReader(new InputStreamReader(toServerSocket.getInputStream()));
	  outClient = new PrintWriter(toServerSocket.getOutputStream(),true);
	}catch (IOException e){
	  System.out.println(e);
	}

}//End Cosntructor

public void playTrivia(){
	setLayout(new BorderLayout(300,300)); //Set Border layout
	//Create JPanels
	panel2 = new JPanel();
	panel3 = new JPanel();
	panel4 = new JPanel();
	panel5 = new JPanel();
	panel6 = new JPanel();
	//Arrange pannels
	panel4.add(panel2);
	panel4.add(panel5);
	panel4.add(panel3, BorderLayout.NORTH);
	panel4.add(panel6, BorderLayout.NORTH);
	panel2.setBackground(Color.GREEN);//Set Background
	panel3.setBackground(Color.CYAN);//Set Background
	panel6.setBackground(Color.CYAN);//Set Background
	//Set JLabels
	lbl2 = new JLabel("Your Answer: ");
	lbl3 = new JLabel("Correct Answer: ");
	lbl4 = new JLabel("Question Score: ");
	lbl5 = new JLabel("Total Score: ");
	lbl6 = new JLabel("Answer questions.You have ten seconds for each question. Three out of 4 answers are right", JLabel.CENTER);
	lbl7 = new JLabel();
	lbl8 = new JLabel("Player's Name: ");
	lbl9 = new JLabel();
	//create JTextFields
	jtext2 = new JTextField(7);
	jtext3 = new JTextField(7);
	jtext4 = new JTextField(7);
	jtext5 = new JTextField(7);
	//Make JText not editable
	jtext2.setEditable(false);
	jtext3.setEditable(false);
	jtext4.setEditable(false);
	jtext5.setEditable(false);
	jtext5.requestFocus();
	//Create JTextArea for handling questions and answers
	jTextArea = new JTextArea(10,50);
	jTextArea2 = new JTextArea(10,20);
	//Add text to panels
	panel3.add(lbl8);
	panel3.add(lbl9);
	panel3.add(lbl2);
	panel3.add(jtext2);
	panel3.add(lbl3);
	panel3.add(jtext3);


	panel6.add(lbl4);
	panel6.add(jtext4);
	panel6.add(lbl5);
	panel6.add(jtext5);
	panel6.add(lbl7);
	panel5.add(lbl6);
	add(panel4, BorderLayout.CENTER);
	//add(panel4);
    panel4.revalidate();
    jTextArea.setEditable(false);//No editing
    jTextArea2.setEditable(false);//No editing

	try{

	  //Loop 10 times to get questions and answers from the client
	  //let the user answer, validate the answers, re-prompt the user
	  //if format is incorrect and then send the answers to the server
	  //to be marked, then receive the feed back fro the server and display
	  //it to the user


	  String name = inClient.readLine();//Read from

	  String yourName = JOptionPane.showInputDialog(null,name);
	  outClient.println(yourName);//
	  String nameFromServer = inClient.readLine();//

	  lbl9.setForeground(Color.red);
	  lbl9.setText(nameFromServer);

	  index = Integer.parseInt(inClient.readLine());
	  outClient.println("yes");
	  String result = "";//Read from server

	  for(int begin = index ; begin < 10; begin++){
	  	for(int j = 0; j < 5; j++){
		  result = inClient.readLine();
		  result += "\n";
		  jTextArea.append(result);//Append it to JTextField;
	  	}

        panel2.add(jTextArea);
		panel4.revalidate();

		//Create a thread for timing-about 10 seconds
	    Thread thread = new Thread(new Runnable(){
	      @Override
	      public void run(){

	        getAnswerFromUser();//Get the answer fro user
	        sendAnswerToServer();//Send to server
	      }

	    });
	    thread.start();//Start threat

	    long endTimeMillis = System.currentTimeMillis() + 15000;

	    while (thread.isAlive()) {
  		    if (System.currentTimeMillis() > endTimeMillis) {
		    isDone = true;// set a time out flag
		    break;
  		    }
	    }//End of while
  	  try {
	    Thread.sleep(500);
	   if(isDone){
	   	outClient.println("0");	//If the user timed out-answer is 0
	   	//optionPane.setVisible(false);
	  	isDone = false;	 //turn the flag off
	  	thread = null;

	   }


      }//end try
	  catch (InterruptedException t) {
	  }
	    String wrongAnswer = inClient.readLine();
	    String points = inClient.readLine();
	    String totalPoints = inClient.readLine();
	    jtext2.setText(userInput);
	    jtext3.setText(wrongAnswer);
	    jtext4.setText(points);
	    jtext5.setText(totalPoints);
	    lbl6.setText("");
	    jTextArea.setText("");


	  }//End outer FOR

	        String finalWord = inClient.readLine();
        	jTextArea.setText("");
        	lbl6.setText("");
        	lbl7.setForeground(Color.red);
        	lbl7.setFont(new Font("Serif", Font.BOLD, 36));
        	lbl7.setText(finalWord);
        	panel4.revalidate();
        	panel2.setVisible(false);
	 		String numPlayers = inClient.readLine();

	  		String results = "";
	  	   results += "Players " + numPlayers + "\n";
	       String winners =  inClient.readLine();
		   String scores[] = winners.split(" ");


	       	for (int i = 0; i < scores.length; i++){
	       		results += scores[i] +"\n";
	       	}

	       	jTextArea2.append(results);
	       	panel5.add(jTextArea2);
	       	panel5.revalidate();



	}catch(IOException ex){
		ex.printStackTrace();
	}
	  try {
        outClient.close();
        inClient.close();
        toServerSocket.close();

      }catch(IOException ex){

        System.out.println(ex);

      }
}
//Validate the users string-the user should enter
//the set of numbers a minimum of one and maximum of three
public static boolean isMatching(String str){
	if (str.matches("[0,1,2,3,4]{1,3}") || (str.matches("[0,1,2,3,4]{1,2}"))){
		return true;
	}
	return false;

}
//Output the answer to the server
public  void sendAnswerToServer(){
	outClient.println(userInput);


}
//Obtain the answer from the user
public static synchronized void getAnswerFromUser(){
	String fromUser = optionPane.showInputDialog(null,"please enter your answer");
	while(true){

   if(fromUser == null || fromUser == " "){//If the answer is null
   	userInput = "0";
   	return;
   }

   while(!(isNoRepeat(fromUser))){//Dont allow repeating a digit
   	fromUser = optionPane.showInputDialog(null,"Repeated number-re-enter");
   }

    if(isMatching(fromUser)){//Validate the format
   	userInput = fromUser;
   	return;
   }

   else {
   	fromUser = optionPane.showInputDialog(null,"Format is Incorect-re-enter");
   }

	}


}
//Make sure the user dont cheat by repeating digits-choices
//Loop through the user string and increment individual counters
//as the character matches-then if there is any counter creater
//than one, then its beeb repeated
public static synchronized  boolean isNoRepeat(String str){
	int num = 0, num1 = 0,num2 = 0,num3 = 0,num4 = 0;
	String str0 = "0", str1 = "1",str2 ="2",str3= "3",str4= "4";
	for(int i = 0; i < str.length(); i++){

		if(str.charAt(i) == str0.charAt(0) ){
			num++;
		}

		if(str.charAt(i) == str1.charAt(0) ){
			num1++;
		}
		if(str.charAt(i) == str2.charAt(0) ){
			num2++;
		}
		if(str.charAt(i) == str3.charAt(0) ){
			num3++;
		}
		if(str.charAt(i) == str4.charAt(0) ){
			num4++;
		}

	}//End for

		if(num > 1 || num1 > 1 || num2 > 1 || num3 > 1 || num4 > 1){

			return false;

		}
	return true;
}


//Main method
public static void main(String[] args)throws IOException {
	FooClient d2 = new FooClient();

	d2.setTitle("Trivia Game!!!");
	d2.setSize(1000, 350);
	//d2.pack();
	//d2.setLocationRelativeTo(null);
	d2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	d2.setVisible(true);
	d2.playTrivia();

}

 }// End Class

