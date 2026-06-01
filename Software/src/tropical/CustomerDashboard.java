package tropical;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CustomerDashboard extends JFrame {

    private Customer customer;

    // Panels
    private JPanel mainPanel;
    private JPanel accountPanel;

    private JTextArea warrantyArea;
    private JTextArea accountInfoArea;

    public CustomerDashboard(Customer customer) {
        this.customer = customer;

        setTitle("Customer Dashboard - " + customer.getFname());
        setSize(700, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new CardLayout());

        Color red = new Color(198, 40, 40);
        Color grey = new Color(240, 240, 240);
        Color whiteish = new Color(250, 250, 250);

        UIManager.put("Button.background", red);
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Panel.background", grey);
        UIManager.put("Label.foreground", Color.BLACK);


        mainPanel = new JPanel(new BorderLayout(10, 10));


        warrantyArea = new JTextArea();
        warrantyArea.setEditable(false);
        warrantyArea.setBackground(whiteish);
        warrantyArea.setBorder(BorderFactory.createLineBorder(red, 1));
        refreshWarranties();

        mainPanel.add(new JScrollPane(warrantyArea), BorderLayout.CENTER);


        JPanel rightPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton refundBtn = new JButton("Apply for Refund");
        JButton accountBtn = new JButton("Account Info");

        rightPanel.add(refundBtn);
        rightPanel.add(accountBtn);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, "MAIN");


        accountPanel = new JPanel(new BorderLayout(10, 10));

        accountInfoArea = new JTextArea(customer.toString());
        accountInfoArea.setEditable(false);
        accountInfoArea.setBackground(whiteish);
        accountInfoArea.setBorder(BorderFactory.createLineBorder(red, 1));

        accountPanel.add(new JScrollPane(accountInfoArea), BorderLayout.CENTER);

        JPanel accBtns = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton changePassBtn = new JButton("Change Password");
        JButton logoutBtn = new JButton("Logout");
        JButton backBtn = new JButton("Back");

        accBtns.add(changePassBtn);
        accBtns.add(logoutBtn);
        accBtns.add(backBtn);

        accountPanel.add(accBtns, BorderLayout.EAST);

        add(accountPanel, "ACCOUNT");

        refundBtn.addActionListener(e -> submitRefund());
        accountBtn.addActionListener(e -> showAccountPage());
        backBtn.addActionListener(e -> showMainPage());

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        changePassBtn.addActionListener(e -> changePassword());

        setVisible(true);
    }

    private void refreshWarranties() {
        List<Warranty> warranties = FileHandler.getCustomerWarranties(customer.getTrn());

        StringBuilder sb = new StringBuilder();
        sb.append("=== YOUR WARRANTIES ===\n\n");

        if (warranties.isEmpty()) {
            sb.append("You currently have no warranties.\n");
        } else {
            for (Warranty w : warranties) {
                sb.append(w.toString()).append("\n-------------------------\n");
            }
        }
        warrantyArea.setText(sb.toString());
    }

    private void submitRefund() {
        String reason = JOptionPane.showInputDialog(this, "Enter refund reason:");

        if (reason == null || reason.trim().isEmpty()) return;

        RefundRequest req = new RefundRequest(customer.getTrn(), reason, reason, reason);
        FileHandler.saveRefundRequest(req);

        JOptionPane.showMessageDialog(this, "Refund request submitted.\nID: " + req.getId());
    }

    private void showAccountPage() {
        accountInfoArea.setText(customer.toString());
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "ACCOUNT");
    }


    private void showMainPage() {
        refreshWarranties();
        CardLayout cl = (CardLayout) getContentPane().getLayout();
        cl.show(getContentPane(), "MAIN");
    }

    
    private void changePassword() {
        String newPass = JOptionPane.showInputDialog(this, "Enter new password:");

        if (newPass == null || newPass.trim().isEmpty()) return;

        boolean ok = FileHandler.updateCustomerPassword(customer.getTrn(), newPass);

        if (ok)
            JOptionPane.showMessageDialog(this, "Password updated successfully.");
        else
            JOptionPane.showMessageDialog(this, "Error updating password.");
    }
}
