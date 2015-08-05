@XmlRootElement(name = "user")
public class User {

  public int id;

  private String name;

  public Long accountNumber;

  public double amount;

  private Address address;

  private String[] phoneNumbers;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Address getAddress() {
    return address;
  }

  public void setAddress(Address address) {
    this.address = address;
  }

  @XmlElement(name="phoneNumber")
  public String[] getPhoneNumbers() {
    return phoneNumbers;
  }

  public void setPhoneNumbers(String[] phoneNumbers) {
    this.phoneNumbers = phoneNumbers;
  }

}