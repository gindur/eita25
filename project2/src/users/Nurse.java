package users;

import java.util.ArrayList;
import java.util.List;

public class Nurse extends Person{
    private List<Patient> patients;
    private String field;

    public Nurse(String name, String hospital, String field){
        super(name, hospital);
        this.patients = new ArrayList<>();
        this.field = field;
        
    }

    public void addPatient(Patient patient){
        patients.add(patient);
    }

    public String getField(){
        return field;
    }

    @Override
    public String getRole() {
        return "Nurse";
    }

    public List<Patient> getPatients(){
        return patients;
    }
    
}
