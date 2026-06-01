package tropical;

public class Worker extends Users {
    private String employeeID;

    public Worker(String trn, String password, String employeeID) {
        super(trn, password, "WORKER");
        this.employeeID = employeeID;
    }

    public String getEmployeeID() { return employeeID; }
}
