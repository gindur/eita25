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
      String succ = "Read success";
      String fail = "Read denied";
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

  
  
   

     
}
