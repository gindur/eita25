package users;

public abstract class Person {
    private String id;
    private String name;
    private String hospital;
    private boolean isAuthenticated; 

    public Person(String name, String hospital, String id){
        this.id = id;
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

    public String getId(){
        return id;
    }

    public abstract String getRole();

    public String toString(){
        return getRole() + ": " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Person person = (Person) obj;
        return id.equals(person.id);
    }




    

}
