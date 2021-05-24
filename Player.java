import java.util.Date;

//TODO create a maven project

public class Player {
    private String playerName;
    private int count;
    private Date startTime;
    private Date endTime;
    private int score ;

    public int getScore() {
        return score;
    }

    public void setScore(){
        this.score=(int)(endTime.getTime()-startTime.getTime())/getCount(); //gettime() returns long
    }

    public int getSeconds(){
        return endTime.getSeconds()-startTime.getSeconds();
    }

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public Date getEndTime() {
        return endTime;
    }
    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

 
}
