@Test
@WebTest()
public void wdshDemo() throws ExecutionException {
  //A selenium web driver session will get open here.
  Grid.driver().get("https://paypal.com");

  //initialize the WebDriverSessionHandler.
  WebDriverSessionHandler m = new WebDriverSessionHandler(Grid.driver());
  //start the WebDriverSessionHandler to handle the selenium session.

  m.start();
  //get the credentials from some database tables and this takes more time
  //than the default WebDriver session timeout value.
  Connection con = DriverManager.getConnection(DB_URL,USER,PASS);
  Statement stmt =
    con.prepareStatement("select * from table_user where id = 1212 ");
  ResultSet rs = stmt.executeQuery();
  ResultSet rs = stmt.executeQuery();
  User user= new User();
  while(rs.next()){
    user.setUserName(rs.getString("userName");
    user.setUserName(rs.getString("password");
  }

  //stop the WebDriverSessionHandler which leaves the WebDriver session to
  //the default value.
  m.stop();

  //......then continue other Web flow ...

  //supply the user credentials
  Grid.driver().findElement(By.id("Email")).sendKeys(user.getUserName());
  Grid.driver().findElement(By.id("Passwd")).sendKeys(user.getUserName());
  Grid.driver().findElement(By.id("signIn")).click();
}