package users;

public class Patient extends Person {
    private String field;

    public Patient(String name, String hospital, String field){
        super(name, hospital);
        this.field = field;        
    }

    @Override
    public String getRole() {
        return "Patient";
    }
    
}
