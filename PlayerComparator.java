
/*Program Name: Player.java
 *Programming Language: Java
 *Author: John Sainna
 *Program Description: Comparator class to compare players scores
 */
import java.util.Comparator;
public class PlayerComparator implements Comparator<Player>{

    @Override
    public int compare(Player player1, Player player2) {

        int score1 = player1.getScore();
        int score2 = player2.getScore();

        if (score1 < score2){
            return +1;
        }else if (score1 > score2){
            return -1;
        }else{
            return 0;
        }
    }
}

