package src.util;
import java.util.ArrayList;
import java.util.List;

import src.users.*;

public class UserManager{
    private List<Person> persons;

    public UserManager(){
        persons = new ArrayList<>();
        Patient x = new Patient("X", "Lund", "heart");
        Patient y = new Patient("Y", "Lund", "heart");
        Patient z = new Patient("Z", "Lund", "brain");

        Doctor a = new Doctor("Johansson", "Lund", "heart");
        a.addPatient(x);
        a.addPatient(y);

        Doctor smith = new Doctor("Smith", "Lund", "brain"); 
        smith.addPatient(z);

        Nurse larsson = new Nurse("Larsson", "Lund", "heart");
        Nurse andreasson = new Nurse("Andreasson", "Lund", "brain");
        larsson.addPatient(x);
        andreasson.addPatient(z);
        andreasson.addPatient(y);

        persons.add(x);
        persons.add(y);
        persons.add(z);
        persons.add(smith);
        persons.add(larsson);
        persons.add(andreasson);
    }

    

}