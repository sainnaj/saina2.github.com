/*Program Name: Player.java
 *Date: March,29 2013
 *Programming Language: Java
 *Author: John Sainna
Description: Data structure to store players name and scores

*/
public class Player{
	private String name = "";//Name of player
	private int score = 0;//Scores of player

	public Player(String name, int score){
		this.name = name;
		this.score = score;
	}


	public void setName(String name){
		this.name = name;
	}
	public void setScore(String name){
		this.score = score;
	}
	public String getName(){
		return name;
	}

	public int getScore(){
		return score;
	}
}