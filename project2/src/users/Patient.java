package users;

public class Patient extends Person {
    private String field;

    public Patient(String name, String hospital, String field, String id){
        super(name, hospital, id);
        this.field = field;        
    }

    @Override
    public String getRole() {
        return "Patient";
    }
    
}
