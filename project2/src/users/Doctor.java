package users;

import java.util.ArrayList;
import java.util.List;

import data.Record;

public class Doctor extends Person{
    private List<Patient> patients;
    private String field;

    public Doctor(String name, String hospital, String field){
        super(name, hospital);
        this.patients = new ArrayList<>();
        this.field = field;
        
    }

    public void addPatient(Patient patient){
        patients.add(patient);
    }

    public void createRec(Nurse nurse, Patient patient,String content ){
        Record rec = new Record(this, nurse, patient, getHospital(), field, content);

        // send to server?
    }
    
    
}
