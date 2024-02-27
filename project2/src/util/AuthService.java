package util;

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

     public Record readRecord(Person person, Patient patient) {
      String succ = "Read record success";
      String fail = "Read record denied";
      Record rec = dbm.getRecord(patient);
  
      boolean hasAccess = false;
  
      if (person instanceof Doctor && (rec.getField().equals(((Doctor) person).getField()) || rec.getDoctorId().equals(((Doctor)person).getId()))) {
          hasAccess = true;
      } else if (person instanceof Nurse && rec.getField().equals(((Nurse) person).getField())|| rec.getNurseId().equals(((Nurse)person).getId())) {
          hasAccess = true;
      } else if (person instanceof Patient && person.equals(patient)) {
          hasAccess = true;
      } else if (person instanceof Government) {
          hasAccess = true;
      }
  
      if (hasAccess) {
          dbm.log(person, succ, patient);
          return rec;
      } else {
          dbm.log(person, fail, patient);
          return null;
      }
   }
   
   // Success => True
   // Denied => False
   public boolean writeRecord(Person person, Patient patient, String content){
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

   public boolean createRecord(Person person, Patient patient, Nurse nurse) {
      String succ = "Create record success";
      String fail = "Create record denied";
      boolean hasAccess = false;
  
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
  

   public boolean deleteRecord(Person person, Patient patient) {
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






  
   

     
}
