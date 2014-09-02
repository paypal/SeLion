public class MyDataSheet {
  private String testcaseid;
  private String country;
  private String currency;

  public void setTestcaseID(String testcaseid) {
    this.testcaseid = testcaseid;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getTestcaseID() {
    return testcaseid;
  }

  public Country getCountry() {
    return Country.getCountry(this.country);
  }

  public Info getCurrency() {
    return Info.getInstance(this.currency);
  }

  public String toString() {
    return "[testcaseid: " + getTestcaseID().toString() + ", country: "
      + getCountry().toString() + ", currency: " + getCurrency().toString() +
       "]";
  }

}