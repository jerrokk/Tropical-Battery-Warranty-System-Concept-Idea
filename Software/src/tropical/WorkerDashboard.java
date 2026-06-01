package tropical;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;

public class WorkerDashboard extends JFrame {

    private Worker worker;

    private JTextField searchTrn;
    private JButton searchBtn;
    private JTextArea detailsArea;

    //Warranty fields
    private JTextField wCustTrn;
    private JComboBox<String> batteryType;
    private JTextField wSerial; // auto-generated
    private JTextField wPurchaseDate, wExpiryDate; // format YYYY-MM-DD
    private JButton addWarrantyBtn;

    // Refund request buttons
    private JButton viewRefundsBtn;
    private JButton processRefundBtn;

    private JButton changePassBtn;

    public WorkerDashboard(Worker worker) {
        this.worker = worker;
        setTitle("Worker Dashboard - " + worker.getEmployeeID());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 550);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        top.add(new JLabel("Search Customer by TRN:"));
        searchTrn = new JTextField(12);
        top.add(searchTrn);
        searchBtn = new JButton("Search");
        top.add(searchBtn);

        JButton logout = new JButton("Logout");
        top.add(logout);

        changePassBtn = new JButton("Change Customer Password");
        top.add(changePassBtn);

        add(top, BorderLayout.NORTH);

        detailsArea = new JTextArea();
        detailsArea.setEditable(false);
        add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        // Warranty form
        JPanel warrantyPanel = new JPanel(new GridLayout(8, 2, 6, 6));
        warrantyPanel.setBorder(BorderFactory.createTitledBorder("Add Warranty"));

        wCustTrn = new JTextField();
        batteryType = new JComboBox<>(new String[]{
                "BKE - Bike (4 weeks)",
                "CAR - Car (12 weeks)",
                "TRK - Truck (24 weeks)",
                "GEN - General (8 weeks)"
        });
        wSerial = new JTextField();
        wSerial.setEditable(false);
        wPurchaseDate = new JTextField();
        wPurchaseDate.setToolTipText("YYYY-MM-DD");
        wExpiryDate = new JTextField();
        wExpiryDate.setToolTipText("Auto-calculated");
        addWarrantyBtn = new JButton("Add Warranty");

        warrantyPanel.add(new JLabel("Customer TRN:"));
        warrantyPanel.add(wCustTrn);
        warrantyPanel.add(new JLabel("Battery Type:"));
        warrantyPanel.add(batteryType);
        warrantyPanel.add(new JLabel("Serial Number:"));
        warrantyPanel.add(wSerial);
        warrantyPanel.add(new JLabel("Purchase Date (YYYY-MM-DD):"));
        warrantyPanel.add(wPurchaseDate);
        warrantyPanel.add(new JLabel("Expiry Date:"));
        warrantyPanel.add(wExpiryDate);
        warrantyPanel.add(new JLabel(""));
        warrantyPanel.add(addWarrantyBtn);

        right.add(warrantyPanel);

        // Refund requests panel
        JPanel refundPanel = new JPanel(new GridLayout(2, 1, 6, 6));
        refundPanel.setBorder(BorderFactory.createTitledBorder("Refund Requests"));
        viewRefundsBtn = new JButton("View All Refund Requests");
        processRefundBtn = new JButton("Approve/Deny Refund");
        refundPanel.add(viewRefundsBtn);
        refundPanel.add(processRefundBtn);

        right.add(refundPanel);

        add(right, BorderLayout.EAST);

        
        searchBtn.addActionListener(e -> searchCustomer());
        logout.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        changePassBtn.addActionListener(e -> changeCustomerPassword());

        addWarrantyBtn.addActionListener(e -> addWarranty());
        batteryType.addActionListener(e -> generateSerialAndExpiry());

        // Refund actions
        viewRefundsBtn.addActionListener(e -> showRefundRequests());
        processRefundBtn.addActionListener(e -> processRefundRequest());
    }

    //SEARCH CUSTOMER
    private void searchCustomer() {
        String trn = searchTrn.getText().trim();
        if (trn.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter TRN to search.");
            return;
        }
        Customer c = FileHandler.findCustomerByTRN(trn);
        if (c == null) {
            detailsArea.setText("Customer not found for TRN: " + trn);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("=== Customer Details ===\n");
        sb.append(c.toString()).append("\n\n");

        List<Warranty> list = FileHandler.getCustomerWarranties(trn);
        if (list.isEmpty()) {
            sb.append("No warranties found for this customer.\n");
        } else {
            sb.append("--- Warranties ---\n");
            for (Warranty w : list) {
                sb.append(w.toString()).append("\n-----------------\n");
            }
        }

        detailsArea.setText(sb.toString());
    }

    //ADD WARRANTY
    private void addWarranty() {
        try {
            String trn = wCustTrn.getText().trim();
            if (trn.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter customer TRN.");
                return;
            }
            LocalDate pur = LocalDate.parse(wPurchaseDate.getText().trim());
            LocalDate exp = LocalDate.parse(wExpiryDate.getText().trim());

            Warranty w = new Warranty(trn, batteryType.getSelectedItem().toString(), wSerial.getText(), pur, exp);
            boolean ok = FileHandler.saveWarranty(w);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Warranty added.");
                wCustTrn.setText("");
                wSerial.setText("");
                wPurchaseDate.setText("");
                wExpiryDate.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Error saving warranty.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Invalid date or error: " + ex.getMessage());
        }
    }

    //GENERATE SERIAL AND EXPIRY
    private void generateSerialAndExpiry() {
        int serial = (int) (Math.random() * 9000000) + 1000000;
        wSerial.setText(String.valueOf(serial));

        String type = (String) batteryType.getSelectedItem();
        LocalDate purchase = LocalDate.now();
        int weeks = 8;
        if (type != null) {
            if (type.startsWith("BKE")) weeks = 4;
            else if (type.startsWith("CAR")) weeks = 12;
            else if (type.startsWith("TRK")) weeks = 24;
            else if (type.startsWith("GEN")) weeks = 8;
        }
        LocalDate expiry = purchase.plusWeeks(weeks);
        wPurchaseDate.setText(purchase.toString());
        wExpiryDate.setText(expiry.toString());
    }

    //CHANGE CUSTOMER PASSWORD
    private void changeCustomerPassword() {
        String trn = JOptionPane.showInputDialog(this, "Enter Customer TRN:");
        if (trn == null || trn.isEmpty()) return;

        String newPass = JOptionPane.showInputDialog(this, "Enter New Password:");
        if (newPass == null || newPass.isEmpty()) return;

        boolean success = FileHandler.updateCustomerPassword(trn, newPass);
        if (success) {
            JOptionPane.showMessageDialog(this, "Password updated successfully.");
        } else {
            JOptionPane.showMessageDialog(this, "Customer not found.");
        }
    }

    //VIEW REFUND REQUESTS
    private void showRefundRequests() {
        List<RefundRequest> list = FileHandler.loadRefundRequests();
        StringBuilder sb = new StringBuilder("---- REFUND REQUESTS ----\n\n");
        if (list.isEmpty()) sb.append("No refund requests.\n");
        else {
            for (RefundRequest r : list) {
                sb.append("ID: ").append(r.getId()).append("\n")
                  .append("Customer TRN: ").append(r.getCustomerTRN()).append("\n")
                  .append("Product: ").append(r.getProductName()).append("\n")
                  .append("Serial: ").append(r.getSerialNumber()).append("\n")
                  .append("Reason: ").append(r.getReason()).append("\n")
                  .append("Status: ").append(r.getStatus()).append("\n-----------------\n");
            }
        }
        detailsArea.setText(sb.toString());
    }

    //PROCESS REFUND REQUEST
    private void processRefundRequest() {
        String id = JOptionPane.showInputDialog(this, "Enter Refund Request ID:");
        if (id == null || id.isEmpty()) return;

        RefundRequest r = FileHandler.getRefundRequest(id);
        if (r == null) {
            JOptionPane.showMessageDialog(this, "Refund request not found.");
            return;
        }

        String[] options = {"Approve", "Deny"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "Approve or deny this refund?",
                "Refund Decision",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice == 0) {
            FileHandler.approveRefund(r);
            JOptionPane.showMessageDialog(this, "Refund approved.");
        } else if (choice == 1) {
            FileHandler.denyRefund(r);
            JOptionPane.showMessageDialog(this, "Refund denied and warranty set to NULL.");
        }
    }
}
