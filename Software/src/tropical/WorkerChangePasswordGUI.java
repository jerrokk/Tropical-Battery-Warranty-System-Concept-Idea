package tropical;

import javax.swing.*;
import java.awt.*;

public class WorkerChangePasswordGUI extends JFrame {

    public WorkerChangePasswordGUI() {

        setTitle("Worker: Change Customer Password");
        setSize(400, 200);
        setLayout(new GridLayout(3, 2));
        setLocationRelativeTo(null);

        JTextField trnField = new JTextField();
        JPasswordField newPasswordField = new JPasswordField();
        JButton updateBtn = new JButton("Update");

        updateBtn.addActionListener(e -> {
            String trn = trnField.getText().trim();
            String newPass = new String(newPasswordField.getPassword()).trim();

            if (trn.isEmpty() || newPass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields must be filled!");
                return;
            }

            PasswordUpdateService service = new PasswordUpdateService();
            boolean success = service.updateCustomerPassword(trn, newPass);

            if (success)
                JOptionPane.showMessageDialog(this, "Password updated successfully!");
            else
                JOptionPane.showMessageDialog(this, "Customer TRN not found!");
        });

        add(new JLabel("Customer TRN:"));
        add(trnField);
        add(new JLabel("New Password:"));
        add(newPasswordField);
        add(new JLabel(""));
        add(updateBtn);

        setVisible(true);
    }
}
