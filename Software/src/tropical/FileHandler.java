package tropical;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + File.separator + "users.txt";
    private static final String CUSTOMERS_FILE = DATA_DIR + File.separator + "customers.txt";
    private static final String WORKERS_FILE = DATA_DIR + File.separator + "workers.txt";
    private static final String WARRANTIES_FILE = DATA_DIR + File.separator + "warranties.txt";
    private static final String SEP = "\\|";
    private static final String JOIN = "|";
    private static final String REFUNDS_FILE = DATA_DIR + File.separator + "refunds.txt";


    public static boolean isValidTRN(String trn) {
        return trn != null && trn.matches("\\d{9}");
    }

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    // Ensure data folder / files

    static {
        try {
            File dir = new File(DATA_DIR);
            if (!dir.exists()) dir.mkdirs();
            new File(USERS_FILE).createNewFile();
            new File(CUSTOMERS_FILE).createNewFile();
            new File(WORKERS_FILE).createNewFile();
            new File(WARRANTIES_FILE).createNewFile();
            new File(REFUNDS_FILE).createNewFile();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // USER EXISTS

    public static boolean userExists(String trn) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 2 && p[1].equals(trn)) return true;
            }
        } catch (IOException ignored) {}
        return false;
    }


    // SAVE USER

    public static boolean saveUser(Users user) {
        if (userExists(user.getTrn())) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(USERS_FILE, true))) {

            if (user instanceof Customer) {
                Customer c = (Customer) user;

                // USERS FILE
                bw.write("CUSTOMER" + JOIN + c.getTrn() + JOIN + c.getPassword()
                        + JOIN + c.getFname() + JOIN + c.getLname()
                        + JOIN + c.getEmail() + JOIN + c.getContactNumber());
                bw.newLine();

                // CUSTOMERS FILE
                try (BufferedWriter cbw = new BufferedWriter(new FileWriter(CUSTOMERS_FILE, true))) {
                    cbw.write(c.getTrn() + JOIN + c.getFname() + JOIN + c.getLname()
                            + JOIN + c.getEmail() + JOIN + c.getContactNumber());
                    cbw.newLine();
                }

            } else if (user instanceof Worker) {
                Worker w = (Worker) user;

                bw.write("WORKER" + JOIN + w.getTrn() + JOIN + w.getPassword()
                        + JOIN + w.getEmployeeID());
                bw.newLine();

                try (BufferedWriter wbw = new BufferedWriter(new FileWriter(WORKERS_FILE, true))) {
                    wbw.write(w.getTrn() + JOIN + w.getEmployeeID());
                    wbw.newLine();
                }

            } else {
                bw.write("USER" + JOIN + user.getTrn() + JOIN + user.getPassword() + JOIN + user.getRole());
                bw.newLine();
            }

            return true;

        } catch (IOException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // LOGIN

    public static Users login(String trn, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(USERS_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 3 && p[1].equals(trn) && p[2].equals(password)) {

                    switch (p[0]) {
                        case "CUSTOMER":
                            return loadCustomerProfile(trn, password);

                        case "WORKER":
                            return loadWorkerProfile(trn, password);

                        default:
                            return new Users(trn, password, p[0]);
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Login error: " + e.getMessage());
        }
        return null;
    }


    // LOAD CUSTOMER PROFILE

    private static Customer loadCustomerProfile(String trn, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 5 && p[0].equals(trn)) {
                    return new Customer(trn, password, p[1], p[2], p[3], p[4]);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading customer: " + e.getMessage());
        }

        return new Customer(trn, password, "", "", "", "");
    }

    // LOAD WORKER PROFILE

    private static Worker loadWorkerProfile(String trn, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(WORKERS_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 2 && p[0].equals(trn)) {
                    return new Worker(trn, password, p[1]);
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading worker: " + e.getMessage());
        }

        return new Worker(trn, password, "");
    }

    // SAVE WARRANTY

    public static boolean saveWarranty(Warranty w) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WARRANTIES_FILE, true))) {

            bw.write(w.getCustomerTRN() + JOIN + w.getProductName() + JOIN
                    + w.getSerialNumber() + JOIN + w.getPurchaseDate()
                    + JOIN + w.getExpiryDate());
            bw.newLine();
            return true;

        } catch (IOException e) {
            System.out.println("Error saving warranty: " + e.getMessage());
            return false;
        }
    }

    public static List<Warranty> loadWarranties() {
        List<Warranty> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(WARRANTIES_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 5) {
                    list.add(new Warranty(
                            p[0], p[1], p[2], LocalDate.parse(p[3]), LocalDate.parse(p[4])));
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading warranties: " + e.getMessage());
        }

        return list;
    }

    public static List<Warranty> getCustomerWarranties(String trn) {
        List<Warranty> all = loadWarranties();
        List<Warranty> result = new ArrayList<>();

        for (Warranty w : all) {
            if (w.getCustomerTRN().equals(trn)) result.add(w);
        }
        return result;
    }

    // FIND CUSTOMER

    public static Customer findCustomerByTRN(String trn) {
        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 5 && p[0].equals(trn)) {
                    return new Customer(p[0], "", p[1], p[2], p[3], p[4]);
                }
            }

        } catch (IOException e) {
            System.out.println("Error finding customer: " + e.getMessage());
        }

        return null;
    }


    // LOAD CUSTOMER LIST
    public static List<Customer> loadCustomerList() {
        List<Customer> customers = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CUSTOMERS_FILE))) {
            String line;

            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 5) {
                    customers.add(new Customer(p[0], "", p[1], p[2], p[3], p[4]));
                }
            }

        } catch (IOException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }

        return customers;
    }


    // UPDATE CUSTOMER PASSWORD
    public static boolean updateCustomerPassword(String trn, String newPassword) {

        boolean customerUpdated = false;
        boolean userUpdated = false;


        // Update USERS FILE

        File users = new File(USERS_FILE);
        File tempUsers = new File(DATA_DIR + "/users_temp.txt");

        try (BufferedReader r = new BufferedReader(new FileReader(users));
             BufferedWriter w = new BufferedWriter(new FileWriter(tempUsers))) {

            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 3 && p[1].equals(trn)) {
                    p[2] = newPassword; // update pass
                    userUpdated = true;
                }

                w.write(String.join(JOIN, p));
                w.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error updating user password: " + e.getMessage());
        }

        users.delete();
        tempUsers.renameTo(users);

        // Update CUSTOMER FILE

        File customers = new File(CUSTOMERS_FILE);
        File tempCust = new File(DATA_DIR + "/customers_temp.txt");

        try (BufferedReader r = new BufferedReader(new FileReader(customers));
             BufferedWriter w = new BufferedWriter(new FileWriter(tempCust))) {

            String line;
            while ((line = r.readLine()) != null) {
                String[] p = line.split(SEP);

                if (p.length >= 5 && p[0].equals(trn)) {
                    // rewrite same customer info (password stored only in USERS file)
                    customerUpdated = true;
                }

                w.write(line);
                w.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error updating customer file: " + e.getMessage());
        }

        customers.delete();
        tempCust.renameTo(customers);

        return userUpdated || customerUpdated;
    }
    
    public static boolean saveRefundRequest(RefundRequest r) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REFUNDS_FILE, true))) {
            // id|customerTRN/productName/serialNumber/reason/status
            bw.write(r.getId() + JOIN + r.getCustomerTRN() + JOIN + r.getProductName() + JOIN
                     + r.getSerialNumber() + JOIN + r.getReason().replace("\n", " ") + JOIN + r.getStatus());
            bw.newLine();
            return true;
        } catch (IOException e) {
            System.out.println("Error saving refund: " + e.getMessage());
            return false;
        }
    }

    public static List<RefundRequest> loadRefundRequests() {
        List<RefundRequest> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(REFUNDS_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 6) {
                    list.add(new RefundRequest(p[0], p[1], p[2], p[3], p[4], p[5]));
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading refunds: " + e.getMessage());
        }
        return list;
    }

    public static RefundRequest getRefundRequest(String id) {
        List<RefundRequest> list = loadRefundRequests();
        for (RefundRequest r : list) if (r.getId().equals(id)) return r;
        return null;
    }

    // overwrite refunds.txt with updated list
    private static boolean writeRefundList(List<RefundRequest> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(REFUNDS_FILE, false))) {
            for (RefundRequest r : list) {
                bw.write(r.getId() + JOIN + r.getCustomerTRN() + JOIN + r.getProductName() + JOIN
                         + r.getSerialNumber() + JOIN + r.getReason().replace("\n"," ") + JOIN + r.getStatus());
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            System.out.println("Error writing refunds: " + e.getMessage());
            return false;
        }
    }

    public static boolean approveRefund(RefundRequest r) {
        List<RefundRequest> list = loadRefundRequests();
        boolean found = false;
        for (RefundRequest req : list) {
            if (req.getId().equals(r.getId())) {
                req.approve();
                found = true;
                break;
            }
        }
        if (found) return writeRefundList(list);
        return false;
    }

    public static boolean denyRefund(RefundRequest r) {
        List<RefundRequest> list = loadRefundRequests();
        boolean found = false;
        for (RefundRequest req : list) {
            if (req.getId().equals(r.getId())) {
                req.deny();
                found = true;
                // also nullify warranty immediately
                markWarrantyNull(req.getSerialNumber());
                break;
            }
        }
        if (found) return writeRefundList(list);
        return false;
    }

    // ----------------- mark warranty product as NULL -----------------
    public static boolean markWarrantyNull(String serial) {
        File input = new File(WARRANTIES_FILE);
        File temp = new File(DATA_DIR + File.separator + "warranties_tmp.txt");
        boolean updated = false;

        try (BufferedReader br = new BufferedReader(new FileReader(input));
             BufferedWriter bw = new BufferedWriter(new FileWriter(temp))) {

            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(SEP);
                if (p.length >= 5 && p[2].equals(serial)) {
                    // set product name to NULL (keep other fields)
                    bw.write(p[0] + JOIN + "NULL" + JOIN + p[2] + JOIN + p[3] + JOIN + p[4]);
                    updated = true;
                } else {
                    bw.write(line);
                }
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error nullifying warranty: " + e.getMessage());
            return false;
        }

        // replace file
        if (!input.delete()) {
            // fallback: try to continue but return false
        }
        if (!temp.renameTo(input)) {
            System.out.println("Failed to rename temp warranties file.");
        }
        return updated;
    }
}
