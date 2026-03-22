package Business;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.Serializable;
import java.time.Duration;

public class TimeTrack implements Serializable {
    private final LocalDateTime creationTime;
    private LocalDateTime CompleteTime;
    private Duration TimeTaken;

    public TimeTrack(){
        creationTime = LocalDateTime.now();
    }

    public TimeTrack(LocalDateTime creationTime, LocalDateTime completeTime) {
        this.creationTime = creationTime;
        if (completeTime != null) {
            this.CompleteTime = completeTime;
            this.TimeTaken = Duration.between(creationTime, completeTime);
        }
    }

    public void setCompleteTime(){
        this.CompleteTime = LocalDateTime.now();
        this.TimeTaken = Duration.between(creationTime, CompleteTime);
        
    }

    public String getTimeTakenString(){
        if (TimeTaken == null) {
            throw new IllegalStateException("Complete time is not set yet.");
        }
        long hours = TimeTaken.toHours();
        long minutes = TimeTaken.toMinutesPart();
        long seconds = TimeTaken.toSecondsPart();

        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }
    

    public String getCreateTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDateTime = creationTime.format(formatter);
        return formattedDateTime;
    }

    public LocalDateTime getCreateTime(){
        return this.creationTime;
    }
    
    public LocalDateTime getCompleteTime(){
        return this.CompleteTime;


    }

    public String getCompleteTimeString(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        String formattedDateTime =  CompleteTime.format(formatter);
        return formattedDateTime;
    }

}
