package tropical;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {
    private JTabbedPane tabs;

    private JTextField cTrn, cFname, cLname, cEmail, cContact;
    private JPasswordField cPass;
    private JButton cRegisterBtn;

    private JTextField wTrn, wEmpId;
    private JPasswordField wPass;
    private JButton wRegisterBtn;

    public RegisterFrame() {
        setTitle("Register User");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);

        tabs = new JTabbedPane();

        // Customer Tab
        JPanel cust = new JPanel(new GridLayout(7, 2, 6, 6));
        cust.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cTrn = new JTextField();
        cPass = new JPasswordField();
        cFname = new JTextField();
        cLname = new JTextField();
        cEmail = new JTextField();
        cContact = new JTextField();
        cRegisterBtn = new JButton("Register Customer");

        cust.add(new JLabel("TRN:")); cust.add(cTrn);
        cust.add(new JLabel("Password:")); cust.add(cPass);
        cust.add(new JLabel("First Name:")); cust.add(cFname);
        cust.add(new JLabel("Last Name:")); cust.add(cLname);
        cust.add(new JLabel("Email:")); cust.add(cEmail);
        cust.add(new JLabel("Contact No:")); cust.add(cContact);
        cust.add(new JLabel()); cust.add(cRegisterBtn);

        cRegisterBtn.addActionListener(e -> registerCustomer());

        // Worker Tab
        JPanel work = new JPanel(new GridLayout(4, 2, 6, 6));
        work.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        wTrn = new JTextField();
        wPass = new JPasswordField();
        wEmpId = new JTextField();
        wRegisterBtn = new JButton("Register Worker");

        work.add(new JLabel("TRN:")); work.add(wTrn);
        work.add(new JLabel("Password:")); work.add(wPass);
        work.add(new JLabel("Employee ID:")); work.add(wEmpId);
        work.add(new JLabel()); work.add(wRegisterBtn);

        wRegisterBtn.addActionListener(e -> registerWorker());

        tabs.addTab("Customer", cust);
        tabs.addTab("Worker", work);

        add(tabs, BorderLayout.CENTER);

        JButton back = new JButton("Back to Login");
        back.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        add(back, BorderLayout.SOUTH);
    }

    private void registerCustomer() {
        String trn = cTrn.getText().trim();
        String pass = new String(cPass.getPassword());
        String fn = cFname.getText().trim();
        String ln = cLname.getText().trim();
        String email = cEmail.getText().trim();
        String contact = cContact.getText().trim();

        if (trn.isEmpty() || pass.isEmpty() || fn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "TRN, Password and First Name are required.");
            return;
        }

        // TRN validation
        if (!FileHandler.isValidTRN(trn)) {
            JOptionPane.showMessageDialog(this, "Invalid TRN. Must be 9 digits.");
            return;
        }

        // Email validation
        if (!email.isEmpty() && !FileHandler.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Invalid email format.");
            return;
        }

        Customer c = new Customer(trn, pass, fn, ln, email, contact);
        boolean ok = FileHandler.saveUser(c);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Customer registered.");
            cTrn.setText(""); cPass.setText(""); cFname.setText(""); cLname.setText(""); cEmail.setText(""); cContact.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "TRN already exists.");
        }
    }

    private void registerWorker() {
        String trn = wTrn.getText().trim();
        String pass = new String(wPass.getPassword());
        String emp = wEmpId.getText().trim();

        if (trn.isEmpty() || pass.isEmpty() || emp.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields required.");
            return;
        }

        // TRN validation
        if (!FileHandler.isValidTRN(trn)) {
            JOptionPane.showMessageDialog(this, "Invalid TRN. Must be 9 digits.");
            return;
        }

        Worker w = new Worker(trn, pass, emp);
        boolean ok = FileHandler.saveUser(w);

        if (ok) {
            JOptionPane.showMessageDialog(this, "Worker registered.");
            wTrn.setText(""); wPass.setText(""); wEmpId.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "TRN already exists.");
        }
    }
}
