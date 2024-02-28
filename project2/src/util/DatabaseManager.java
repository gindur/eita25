package util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import users.*;

public class DatabaseManager{
    private List<Person> persons = new ArrayList<>();
    private List<Record> records = new ArrayList<>();

    private final String logPath = "src/database/log.txt";
    private final String recordPath = "src/database/records/";
    
    public DatabaseManager(){

        try{
            //Debugging current directory
            String currentDirectory = new File("").getAbsolutePath();
            System.out.println(currentDirectory);

            File log = new File(logPath);
            log.createNewFile();
        } catch (Exception e) {
            System.out.println("An error occurred starting the database manager");
            e.printStackTrace();
        }
        
    }

    public void initialize(){
        loadRecords();
        loadUsers();
    }

    public void loadUsers(){
        Government gov = new Government("Staten", "Lund", "government1");

        Patient pat1 = new Patient("Löf", "Lund", "heart", "patient2");
        Patient pat2 = new Patient("", "Lund", "brain", "patient3");

        Nurse nur1 = new Nurse("Larsson", "Lund", "heart", "nurse4");
        Nurse nur2 = new Nurse("Andreasson", "Lund", "brain", "nurse5");
        
        Doctor doc1 = new Doctor("Johansson", "Lund", "heart", "doctor6");
        Doctor doc2 = new Doctor("Smith", "Lund", "brain", "doctor7"); 
        



        doc1.addPatient(pat1);
        doc2.addPatient(pat1);
        doc2.addPatient(pat2);

        nur1.addPatient(pat2);
        nur2.addPatient(pat1);

        persons.add(doc1);
        persons.add(doc2);
        persons.add(nur1);
        persons.add(nur2);
        persons.add(pat1);
        persons.add(pat2);
        persons.add(gov);
    }

    public Person getPerson(String id){
        return persons
        .stream()
        .filter(c-> c.getId() == id)
        .findFirst()
        .orElse(null);
    }

    private List<Patient> getPatients(){
        return persons
        .stream()
        .filter(p -> p instanceof Patient)
        .map(p -> (Patient) p)
        .collect(Collectors.toList());
    }

    public void log(Person person, String info, Patient patient){
        try {
            BufferedWriter logger = new BufferedWriter(new FileWriter(logPath, true));
            logger.write(getTime() + " - " + person.toString() +" "+ info +" "+ patient.getName());
            logger.newLine();
            logger.close();
        } catch (IOException e) {
            System.out.println("ERROR: logging did not work.");
            e.printStackTrace();
        }

    }

    public String getTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
        LocalDateTime time = LocalDateTime.now();
        return formatter.format(time);
    }

    public void createRecord(Doctor doc, Nurse nurse, Patient patient, String hospital, String field){
        records.add(new Record(doc, nurse, patient, hospital, field));
        saveRecords();
    }

    public Record getRecord(Patient patient){
        return records
        .stream()
        .filter(r -> r.getPatient().equals(patient))
        .findFirst()
        .orElse(null);
    }

    public boolean deleteRecord(Patient patient){
        File recordFile = new File(recordPath + patient.getId());
        Record rec = getRecord(patient);
        if(rec != null){
            records.remove(rec);
            if(recordFile.exists() && recordFile.delete()){
                return true;
            } else {
                System.out.println("ERROR: can't delete record.");
                return false;
            }
        } else {
            return false;
        }

    }

    public void saveRecords(){
        List<Patient> pats = getPatients();
        for (Patient pat : pats){
            Record rec = getRecord(pat);
            File recordFile = new File(recordPath + pat.getId());
            try {
                FileWriter recordWriter = new FileWriter(recordFile, false);
                recordWriter.write(rec.toString());
                recordWriter.close();
            } catch (IOException e) {
                System.out.println("ERROR: could not save records.");
                e.printStackTrace();
            }
        }
    }

    public void loadRecords(){
        List<Patient> pats = getPatients();

        for (Patient patient: pats){
            File prf = new File(recordPath + patient.getId());
            if (prf.exists()){
                try {
                    Scanner scan = new Scanner(prf);
                    Patient pat= (Patient) getPerson(scan.nextLine().split(":")[1]);
                    Doctor doc = (Doctor) getPerson(scan.nextLine().split(":")[1]);
                    Nurse nur = (Nurse) getPerson(scan.nextLine().split(":")[1]);
                    String hos = scan.nextLine().split(":")[1];
                    String field = scan.nextLine().split(":")[1];

                    Record rec = new Record(doc, nur, pat, hos, field);
                    scan.nextLine();
                    while(scan.hasNextLine()){
                        String text = scan.nextLine();
                        if (text != ""){
                            String[] entry = text.split(",");
                            rec.addEntry(entry[0], entry[1]);
                        }
                    }
                    records.add(rec);
                    scan.close();
                } catch (FileNotFoundException e){
                    System.out.println("ERROR: could not load records.");
                    e.printStackTrace();
                }
            }
            continue;
        }
    }


}