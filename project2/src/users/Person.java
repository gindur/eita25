package src.users;

public abstract class Person {
    private static int id = 0;
    private String name;
    private String hospital;
    private boolean isAuthenticated; 

    public Person(String name, String hospital){
        this.id = id++;
        this.name = name;
        this.hospital = hospital;
        isAuthenticated = false;
    }

    public String getName(){
        return name;
    }

    public String getHospital(){
        return hospital;
    }

    public boolean isAuthenticated(){
        return isAuthenticated;
    }

    

}
