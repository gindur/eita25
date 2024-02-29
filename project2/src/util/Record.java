package util;

import java.util.ArrayList;

import users.*;

public class Record {

    private Doctor doctor;
    private Nurse nurse;
    private Patient patient;
    private String hospital;
    private String field;
    private ArrayList<Entry> entries = new ArrayList<>();

    public Record(Doctor doctor, Nurse nurse, Patient patient, String hospital, String field){
        this.doctor = doctor;
        this.nurse = nurse;
        this.patient = patient;
        this.hospital = hospital;
        this.field = field;
    }

    public Patient getPatient(){
        return patient;
    }

    public ArrayList<Entry> getEntries(){
        return entries;
    }

    public void addEntry(String date, String content){
        entries.add(new Entry(date, content));
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Patient: " + patient.getId() + "\n");
        sb.append("Doctor: " + doctor.getId() + "\n");
        sb.append("Nurse: " + nurse.getId() + "\n");
        sb.append("Hospital: " + hospital + "\n");
        sb.append("Field: " + field + "\n");
        sb.append("Entries: \n");
        for (Entry entry : entries) {
            sb.append(entry.toString() + "\n");
        }
        return sb.toString();
    }

    public String getField(){
        return field;
    }

    public String getDoctorId() {
        return doctor.getId();
    }

    public Doctor getDoctor(){
        return doctor;
    }

    public String getNurseId(){
        return nurse.getId();
    }

    public Nurse getNurse(){
        return nurse;
    }



    
}
