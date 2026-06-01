package tropical;

import java.time.LocalDate;

public class Warranty {
    private String customerTRN;
    private String productName;
    private String serialNumber;
    private LocalDate purchaseDate;
    private LocalDate expiryDate;

    public Warranty(String customerTRN, String productName, String serialNumber,
                    LocalDate purchaseDate, LocalDate expiryDate) {
        this.customerTRN = customerTRN;
        this.productName = productName;
        this.serialNumber = serialNumber;
        this.purchaseDate = purchaseDate;
        this.expiryDate = expiryDate;
    }

    public String getCustomerTRN() { return customerTRN; }
    public String getProductName() { return productName; }
    public String getSerialNumber() { return serialNumber; }
    public LocalDate getPurchaseDate() { return purchaseDate; }
    public LocalDate getExpiryDate() { return expiryDate; }

    public void setExpiryDate(LocalDate newDate) { this.expiryDate = newDate; }
    
    public String toString() {
        return "Product: " + productName + "\nSerial: " + serialNumber +
               "\nPurchase Date: " + purchaseDate + "\nExpiry Date: " + expiryDate;
    }
}
