package tropical;

import java.io.*;
import java.util.ArrayList;

public class PasswordUpdateService {

    public boolean updateCustomerPassword(String trn, String newPassword) {
        ArrayList<String> usersLines = new ArrayList<>();
        ArrayList<String> customersLines = new ArrayList<>();

        boolean found = false;

        //UPDATE users.txt
        try (BufferedReader br = new BufferedReader(new FileReader("data/users.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");

                if (p.length >= 3 && p[1].equals(trn) && p[0].equals("CUSTOMER")) {
                    // Update password
                    found = true;
                    usersLines.add("CUSTOMER|" + trn + "|" + newPassword + "|" +
                                   p[3] + "|" + p[4] + "|" + p[5] + "|" + p[6]);
                } else {
                    usersLines.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading users.txt: " + e.getMessage());
        }

        //UPDATE customers.txt
        try (BufferedReader br = new BufferedReader(new FileReader("data/customers.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split("\\|");

                if (p.length >= 5 && p[0].equals(trn)) {
                    customersLines.add(trn + "|" + p[1] + "|" + p[2] + "|" + p[3] + "|" + p[4]);
                } else {
                    customersLines.add(line);
                }
            }
        } catch (Exception e) {
            System.out.println("Error reading customers.txt: " + e.getMessage());
        }

        if (!found) return false; // TRN not found

        //SAVE FILES BACK
        writeListToFile("data/users.txt", usersLines);
        writeListToFile("data/customers.txt", customersLines);

        return true;
    }

    private void writeListToFile(String filename, ArrayList<String> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String s : list) {
                bw.write(s);
                bw.newLine();
            }
        } catch (Exception e) {
            System.out.println("Error saving file: " + e.getMessage());
        }
    }
}
