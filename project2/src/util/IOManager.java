package util;

import java.util.Arrays;

import users.Doctor;
import users.Government;
import users.Nurse;
import users.Person;

public class IOManager {

    private AuthService authService;

    public IOManager(AuthService authService){
        this.authService = authService;
    }

    public String startMessage(Person person){
        return "Welcome " + person.getName() + " (" + person.getRole() + ")\n" +
        "Here are your possible commands:\n" + showOptions(person);
    }

    public String showOptions(Person person) {
    StringBuilder response = new StringBuilder();
    appendOption(response, "Show this message again: help");
    appendOption(response, "Read record: read <patientId>");

    if (person instanceof Nurse || person instanceof Doctor || person instanceof Government) {
        appendOption(response, "Write to existing record: write <patientId> <journalEntry>");
    }

    if (person instanceof Doctor) {
        appendOption(response, "Create new record: create <patientId> <nurseId>");
    }

    if (person instanceof Government) {
        appendOption(response, "Delete record: delete <patientId>");
    }

    return response.toString();
}

private void appendOption(StringBuilder sb, String option) {
    if (sb.length() > 0) {
        sb.append("\n");
    }
    sb.append(option);
}

public String handleInput(Person person, String input){
    String inputs[] = input.trim().split(" ");
    String command = (input.length() > 0) ? inputs[0] : "";

    String patientId;
    String nurseId;
    String text;

    String cmd = command.toLowerCase();

    try {
        switch(cmd){
            case "read":
                try {
                    String patient = inputs[1];
                    Record rec = authService.readRecord(person, patient);
                    if(rec == null){
                        return "Permission denied or invalid patient ID.";
                    } else {
                        return rec.toString().trim();
                    }
                } catch (ArrayIndexOutOfBoundsException e){
                    return "No patient ID found.";
                }
            case "write":
                patientId = inputs[1];
                String[] words = Arrays.copyOfRange(inputs, 2, inputs.length);
                text = String.join(" ", words);
                if(authService.writeRecord(person, patientId, text)){
                    return "Wrote to record for patient: " + patientId;
                } else {
                    return "Write permission denied";
                }
            case "delete":
                patientId = inputs[1];
                if (authService.deleteRecord(person, patientId)){
                    return "Deleted record for patient: " + patientId;
                } else{
                    return "Delete permission denied";
                }

            case "create":
                patientId = inputs[1];
                nurseId = inputs[2];
                if (authService.createRecord(person, patientId, nurseId)){
                    return "Created record for patient:" + patientId;
                } else{
                    return "Create permission denied";
                }
            case "help":
                showOptions(person);
                return "";
            default:
                return "No matching command found, type <help> to show commands.";
                

        }

    } catch (Exception e) {
        return "There was an error reading your command";
    }

}





    
}
