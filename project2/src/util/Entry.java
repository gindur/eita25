package util;

public class Entry {
    String date;
    String content;

    public Entry(String date, String content){
        this.date = date;
        this.content = content;
    }

    public String toString(){
        return date + ": " + content; 
    }

    public String getDate(){
        return date;
    }

    public String getContent(){
        return content;
    }



}
