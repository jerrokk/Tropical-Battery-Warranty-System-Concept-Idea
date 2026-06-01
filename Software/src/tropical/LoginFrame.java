package tropical;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField trnField;
    private JPasswordField passField;
    private JButton loginBtn, registerBtn;

    public LoginFrame() {
        setTitle("Tropical Battery - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 230);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        form.add(new JLabel("TRN:"));
        trnField = new JTextField();
        form.add(trnField);

        form.add(new JLabel("Password:"));
        passField = new JPasswordField();
        form.add(passField);

        loginBtn = new JButton("Login");
        registerBtn = new JButton("Register");

        form.add(loginBtn);
        form.add(registerBtn);

        add(form, BorderLayout.CENTER);

        loginBtn.addActionListener(e -> attemptLogin());
        registerBtn.addActionListener(e -> {
            dispose();
            new RegisterFrame().setVisible(true);
        });
    }

    private void attemptLogin() {
        String trn = trnField.getText().trim();
        String pass = new String(passField.getPassword());

        Users user = FileHandler.login(trn, pass);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid TRN or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome, " + user.getRole() + "!");
        dispose();
        if (user instanceof Worker) {
            SwingUtilities.invokeLater(() -> new WorkerDashboard((Worker) user).setVisible(true));
        } else if (user instanceof Customer) {
            SwingUtilities.invokeLater(() -> new CustomerDashboard((Customer) user).setVisible(true));
        } else {
            // generic
            JOptionPane.showMessageDialog(null, "Role not handled: " + user.getRole());
            new LoginFrame().setVisible(true);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
