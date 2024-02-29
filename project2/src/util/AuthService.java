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


     public String readRecord(Person person, String patientId) {
      Patient pat = id2Patient(patientId);
      if (pat == null) {
          dbm.log(person, "Read record attempt - Invalid patient ID", patientId);
          return "Invalid patient ID or access denied."; // Generic message for security
      }
  
      Record rec = dbm.getRecord(pat);
      if (rec == null) {
          // Log the attempt but don't disclose the absence of a record
          dbm.log(person, "Read record attempt - No record found", patientId);
          return "No record found for patient " + patientId;
      }
  
      if (hasReadAccess(person, rec, pat)) {
         dbm.log(person, "Read record success", patientId);
         return rec.toString(); // Return the record details
          
      } else {
          dbm.log(person, "Read record denied", patientId);
          return "Access to record denied."; // Generic denial message
      }
  }
  
  

  private boolean hasReadAccess(Person person, Record rec, Patient patient) {
      if (rec == null){
         return false;
      }
      if (person instanceof Doctor) {
          Doctor doc = (Doctor) person;
          return rec.getField().equals(doc.getField()) || rec.getDoctorId().equals(doc.getId());
      } else if (person instanceof Nurse) {
          Nurse nur = (Nurse) person;
          return rec.getField().equals(nur.getField()) || rec.getNurseId().equals(nur.getId());
      } else if (person instanceof Patient && person.equals(patient)) {
          return true;
      } else return person instanceof Government;
  }

   public String writeRecord(Person person, String patientId, String content){
      
      Patient patient = id2Patient(patientId);
      if (patient == null) return "No patient found";
      Record rec = dbm.getRecord(patient);
      if (rec == null) return "No record found";

      String succ = "Write record success";
      String fail = "Write record denied";
      boolean hasAccess = false;
      if (person instanceof Doctor && (rec.getField().equals(((Doctor) person).getField()) || rec.getDoctorId().equals(((Doctor)person).getId()))) {
         hasAccess = true;
     } else if (person instanceof Nurse && rec.getField().equals(((Nurse) person).getField())|| rec.getNurseId().equals(((Nurse)person).getId())) {
         hasAccess = true;
     }
     if (hasAccess) {
      dbm.log(person, succ, patientId);
      rec.addEntry(dbm.getTime(), content);
      dbm.saveRecords();
      return succ;
     } else {
      dbm.log(person, fail, patientId);
      return fail;
     }
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
         dbm.log(person, succ, patientId);
      } else {
          dbm.log(person, fail, patientId);
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
         dbm.log(person, succ, patientId);
      } else {
         dbm.log(person, fail, patientId);
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
