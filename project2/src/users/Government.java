package users;

public class Government extends Person{
    private boolean delete;

    public Government(String name, String hospital, String id){
        super(name, hospital, id);
        this.delete = true;
    }

    @Override
    public String getRole() {
        return "Government";
    }


}
