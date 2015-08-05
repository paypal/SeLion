public class SimpleData {
  private int employeeId;

  private String employeeName;

  public int getEmployeeId () {
    return employeeId;
  }

  public void setEmployeeId (int employeeId) {
    this.employeeId = employeeId;
  }

  public String getEmployeeName () {
    return employeeName;
  }

  public void setEmployeeName (String employeeName) {
    this.employeeName = employeeName;
  }

  @Override
  public String toString () {
    final StringBuilder sb = new StringBuilder("SimpleData{");
    sb.append("employeeId=").append(employeeId);
    sb.append(", employeeName='").append(employeeName).append('\'');
    sb.append('}');
    return sb.toString();
  }
}