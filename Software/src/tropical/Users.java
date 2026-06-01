package tropical;

public class Users {
    protected String trn;
    protected String password;
    protected String role;

    public Users(String trn, String password, String role) {
        this.trn = trn;
        this.password = password;
        this.role = role;
    }

    public String getTrn() { return trn; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    
    public void setPassword(String newPassword) {
        this.password = newPassword;
    }

}
