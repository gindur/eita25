package data;

import users.*;

public class Record {

    private Doctor doctor;
    private Nurse nurse;
    private Patient patient;
    private String hospital;
    private String field;
    private String content;

    public Record(Doctor doctor, Nurse nurse, Patient patient, String hospital, String field, String content){
        this.doctor = doctor;
        this.nurse = nurse;
        this.patient = patient;
        this.hospital = hospital;
        this.field = field;
        this.content = content;
    }

    
}
