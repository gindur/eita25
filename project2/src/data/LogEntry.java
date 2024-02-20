package data;
import java.sql.Time;
import users.Person;

public class LogEntry {
    private Person person;
    private Record record;
    private Time time;

    public LogEntry(Person person, Record record, Time time){
        this.person = person;
        this.record = record;
        this.time = time;
    }

    public Person getPerson(){
        return person;
    }

    public Record getFile(){
        return record;
    }

    public Time getTime(){
        return time;
    }
}
