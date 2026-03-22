package Business;

import java.io.Serializable;

import Base.Resident;
import Base.Staff;

public class Task implements Serializable {
    private String Task_name;
    private String task_Description;
    private int Task_Priority = 0;
    private int TASKID;
    private static int idCounter = 0;
    private String Category;
    private TimeTrack Time;
    private String RoomNum;
    private String Status;
    private Boolean Complete = false;
    private Staff assignee;

    public Resident reporter;

    public Task(String Task_name, String task_Description, String Category, String RoomNum) {
        this.Task_name = Task_name;
        this.task_Description = task_Description;
        this.Category = Category;
        this.Time = new TimeTrack();
        this.RoomNum = RoomNum;
        this.Status = "Task Created";
        this.TASKID = idCounter++;
    }

    public Task(int id, String Task_name, String task_Description, String Category, String RoomNum,
            int priority, String status, boolean complete, TimeTrack time) {
        this.TASKID = id;
        this.Task_name = Task_name;
        this.task_Description = task_Description;
        this.Category = Category;
        this.RoomNum = RoomNum;
        this.Task_Priority = priority;
        this.Status = status;
        this.Complete = complete;
        this.Time = time;
    }

    public void Assign(Staff assignee) {
        this.assignee = assignee;
    }

    public boolean isComplete() {
        return this.Complete;
    }

    public void setTask_name(String task_name) {
        this.Task_name = task_name;
    }

    public void setTask_Description(String task_Description) {
        this.task_Description = task_Description;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public void setTask_Priority(int task_Priority) {
        Task_Priority = task_Priority;
    }

    public void setRoomNum(String Roomnum) {
        this.RoomNum = Roomnum;
    }

    public void TaskStatus(String Status) {
        this.Status = Status;
    }

    public String getTask_name() {
        return this.Task_name;
    }

    public String getTask_Description() {
        return this.task_Description;
    }

    public int getTask_Priority() {
        return Task_Priority;
    }

    public int getTASKID() {
        return TASKID;
    }

    public void setTASKID(int id) {
        this.TASKID = id;
    }

    public String getTask_Category() {
        return this.Category;
    }

    public String getRoomNum() {
        return this.RoomNum;
    }

    public String getStatus() {
        return this.Status;
    }

    public boolean getComplete() {
        return this.Complete;
    }

    public TimeTrack getTimeTrack() {
        return this.Time;
    }

    @Override
    public String toString() {
        return "Task ID: " + getTASKID() + ", Room#: " + getRoomNum() + ", Name: " + getTask_name() + ", Description: "
                + getTask_Description() + ", Category: " + getTask_Category() + ", Priority: " + getTask_Priority()
                + ", Creation Time: " + Time.getCreateTimeString();

    }

    public String TaskComplete() {
        Time.setCompleteTime();
        this.Complete = true;
        return "Task ID " + getTASKID() + ", Room#: " + getRoomNum() + ", Name: " + getTask_name() + ", Description: "
                + getTask_Description() + ", Category: " + getTask_Category() + ", Priority: " + getTask_Priority()
                + ", Completion Time: " + Time.getCompleteTimeString() + ", Time Taken: " + Time.getTimeTakenString();
    }

}
