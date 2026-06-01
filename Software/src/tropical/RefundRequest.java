package tropical;

import java.io.Serializable;
import java.util.UUID;

public class RefundRequest implements Serializable {
    private String id;
    private String customerTRN;
    private String productName;
    private String serialNumber;
    private String reason;
    private String status; // PENDING, APPROVED, DENIED

    // Full constructor for loading from file
    public RefundRequest(String id, String customerTRN, String productName, String serialNumber, String reason, String status) {
        this.id = id;
        this.customerTRN = customerTRN;
        this.productName = productName;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.status = status;
    }

    // Constructor for new requests (id auto-generated, status = PENDING)
    public RefundRequest(String customerTRN, String productName, String serialNumber, String reason) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.customerTRN = customerTRN;
        this.productName = productName;
        this.serialNumber = serialNumber;
        this.reason = reason;
        this.status = "PENDING";
    }

    public String getId() { return id; }
    public String getCustomerTRN() { return customerTRN; }
    public String getProductName() { return productName; }
    public String getSerialNumber() { return serialNumber; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }

    public void approve() { this.status = "APPROVED"; }
    public void deny() { this.status = "DENIED"; }

    @Override
    public String toString() {
        return "ID: " + id +
               "\nCustomer: " + customerTRN +
               "\nProduct: " + productName +
               "\nSerial: " + serialNumber +
               "\nReason: " + reason +
               "\nStatus: " + status;
    }
}
