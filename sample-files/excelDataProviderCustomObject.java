public class CustomData {
  private String employeeName;

  private Country country;

  public String getEmployeeName () {
    return employeeName;
  }

  public void setEmployeeName (String employeeName) {
    this.employeeName = employeeName;
  }

  public Country getCountry () {
    return country;
  }

  public void setCountry (Country country) {
    this.country = country;
  }

  @Override
  public String toString () {
    final StringBuilder sb = new StringBuilder("CustomData{");
    sb.append("employeeName='").append(employeeName).append('\'');
    sb.append(", country=").append(country);
    sb.append('}');
    return sb.toString();
  }
}