package users;

public class Government extends Person{
    private boolean delete;

    public Government(String name, String hospital){
        super(name, hospital);
        this.delete = true;
    }

    @Override
    public String getRole() {
        return "Government";
    }


}
