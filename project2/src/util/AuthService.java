package util;

import javax.print.DocFlavor.STRING;

import users.*;

public class AuthService {
    private DatabaseManager dbm;
    /* 1. Retrieve Username and Password from client
     * 2. Get hashed Password from database with salt
     * 3. Hash retrieved password with salt and compare to stored hash.
     */


     public AuthService(DatabaseManager dbm){
        this.dbm = dbm;
     }


     public Record readRecord(Person person, String patientId) {
      Patient pat = id2Patient(patientId);
      if (pat == null) return null;
      String succ = "Read record success";
      String fail = "Read record denied";
      Record rec = dbm.getRecord(pat);
  
      boolean hasAccess = false;
  
      if (person instanceof Doctor && (rec.getField().equals(((Doctor) person).getField()) || rec.getDoctorId().equals(((Doctor)person).getId()))) {
          hasAccess = true;
      } else if (person instanceof Nurse && rec.getField().equals(((Nurse) person).getField())|| rec.getNurseId().equals(((Nurse)person).getId())) {
          hasAccess = true;
      } else if (person instanceof Patient && person.equals(pat)) {
          hasAccess = true;
      } else if (person instanceof Government) {
          hasAccess = true;
      }
  
      if (hasAccess) {
          dbm.log(person, succ, pat);
          return rec;
      } else {
          dbm.log(person, fail, pat);
          return null;
      }
   }
   
   // Success => True
   // Denied => False
   public boolean writeRecord(Person person, String patientId, String content){
      
      Patient patient = id2Patient(patientId);
      if (patient == null) return false;
      Record rec = dbm.getRecord(patient);

      String succ = "Write record success";
      String fail = "Write record denied";
      boolean hasAccess = false;
      if (person instanceof Doctor && (rec.getField().equals(((Doctor) person).getField()) || rec.getDoctorId().equals(((Doctor)person).getId()))) {
         hasAccess = true;
     } else if (person instanceof Nurse && rec.getField().equals(((Nurse) person).getField())|| rec.getNurseId().equals(((Nurse)person).getId())) {
         hasAccess = true;
     }
     if (hasAccess) {
      dbm.log(person, succ, patient);
      rec.addEntry(dbm.getTime(), content);
      dbm.saveRecords();
     } else {
      dbm.log(person, fail, patient);
     }
     return hasAccess;
   }

   public boolean createRecord(Person person, String patientId, String nurseId) {
      Patient patient = id2Patient(patientId);
      if (patient == null) return false;
      String succ = "Create record success";
      String fail = "Create record denied";
      boolean hasAccess = false;

      Person potentialNurse = dbm.getPerson(nurseId);
      if (!(potentialNurse instanceof Nurse)) {
         System.out.println("The specified ID does not correspond to a nurse.");
         return false;
      }
      Nurse nurse = (Nurse) potentialNurse;
  
      if (person instanceof Doctor && ((Doctor) person).getPatients().contains(patient)) {
         Doctor doc = (Doctor) person;
         hasAccess = true;
         dbm.createRecord(doc, nurse, patient, doc.getHospital(), doc.getField());
         dbm.log(person, succ, patient);
      } else {
          dbm.log(person, fail, patient);
      }
  
      return hasAccess;
  }
  

   public boolean deleteRecord(Person person, String patientId) {
      Person potentialPatient = dbm.getPerson(patientId);
      if (!(potentialPatient instanceof Patient)) {
         System.out.println("The specified ID does not correspond to a patient.");
         return false;
      }
      Patient patient = (Patient) potentialPatient;
      String succ = "Delete record success";
      String fail = "Delete record denied";
      boolean hasAccess = false;

      if (person instanceof Government) {
         hasAccess = true;
         dbm.deleteRecord(patient); 
         dbm.log(person, succ, patient);
      } else {
         dbm.log(person, fail, patient);
      }

      return hasAccess;
   }

   private Patient id2Patient(String id){
      Person potentialPatient = dbm.getPerson(id);
      if (!(potentialPatient instanceof Patient)) {
         System.out.println("The specified ID does not correspond to a patient.");
         return null;
      }
      return (Patient) potentialPatient;
   }





  
   

     
}
