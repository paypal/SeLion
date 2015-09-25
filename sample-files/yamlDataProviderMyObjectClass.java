package mypackage;

public class MyObject{

  private String testcaseid;

  private String country;

  public MyObject() {}

  public String getTestcaseid() {
    return testcaseid;
  }

  public void setTestcaseid(String testcaseid) {
    this.testcaseid = testcaseid;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String toString() {
    return "[testcaseid: " + getTestcaseid().toString() + ", country: "
      + getCountry().toString() + "]";
  }
}