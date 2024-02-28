package users;

import java.util.ArrayList;
import java.util.List;

public class Doctor extends Person{
    private List<Patient> patients;
    private String field;

    public Doctor(String name, String hospital, String field, String id){
        super(name, hospital, id);
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
        return "Doctor";
    }

    public List<Patient> getPatients(){
        return patients;
    }
    
    
}
