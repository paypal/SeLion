@XmlRootElement
public class Address {
  @XmlAttribute
  public int id;

  private String street;

  public Address() {
  }

  public Address(String street) {
    this.setStreet(street);
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street;
  }
}