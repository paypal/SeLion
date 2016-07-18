public class USER {
    private String name;
    private String password;
    private Long accountNumber;
    private Double amount;
    private int size;
    private AREA_CODE[] areaCode;
    private BANK bank;
    private String phoneNumber;

    public USER() {
    }

    public USER(String name, String password, Long accountNumber, Double amount, int size, AREA_CODE[] areaCode,
            BANK bank, String phoneNumber) {
        this.setName(name);
        this.setPassword(password);
        this.setAccountNumber(accountNumber);
        this.setAmount(amount);
        this.setSize(size);
        this.setAreaCode(areaCode);
        this.setBank(bank);
        this.setPhoneNumber(phoneNumber);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Long accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public AREA_CODE[] getAreaCode() {
        return (areaCode == null) ? null : Arrays.copyOf(areaCode, areaCode.length);
    }

    public void setAreaCode(AREA_CODE[] areaCode) {
        this.areaCode = areaCode;
    }

    public BANK getBank() {
        return bank;
    }

    public void setBank(BANK bank) {
        this.bank = bank;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
