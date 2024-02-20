package src.util;
public class AuthService {
    private UserManager um;
    /* 1. Retrieve Username and Password from client
     * 2. Get hashed Password from database with salt
     * 3. Hash retrieved password with salt and compare to stored hash.
     */


     public AuthService(UserManager um){
        this.um = um;
     }

     
}
