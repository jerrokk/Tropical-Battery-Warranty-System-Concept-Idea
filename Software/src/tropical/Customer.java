package tropical;

public class Customer extends Users {
    private String fname, lname, email, contact;

    public Customer(String trn, String password, String fname, String lname,
                    String email, String contact) {
        super(trn, password, "CUSTOMER");
        this.fname = fname;
        this.lname = lname;
        this.email = email;
        this.contact = contact;
    }

    public String getFname() { 
    	return fname; 
    	}
    
    public String getLname() { 
    	return lname; 
    	}
    
    public String getEmail() { 
    	return email; 
    	}
    
    public String getContactNumber() { 
    	return contact; 
    	}
    
    public String toString() {
        return "TRN: " + trn + "\nName: " + fname + " " + lname + 
               "\nEmail: " + email + "\nContact: " + contact;
    }
}
