@Test(groups = {"sessionSharingWebTests"})
@WebTest(sessionName="login-flow",keepSessionOpen=true)
public void testLoginFlow() {
  //supply the user credentials
  Grid.open("http://www.paypal.com");
  //supply the user credentials
  Grid.driver().findElement(By.id("Email")).sendKeys(user.getUserName());
  Grid.driver().findElement(By.id("Passwd")).sendKeys(user.getUserName());
  Grid.driver().findElement(By.id("signIn")).click();
}

@Test(groups = {"sessionSharingWebTests"},dependsOnMethods="testMainFlow")
@WebTest(sessionName="login-flow",openNewSession=false)
public void testSendMoneyFlow1() {
  //Will open up in the same browser window using the same web driver session.
  Grid.open("http://www.paypal.com/paymentFlow1");
  Grid.driver().findElement(By.id("Receiver")).click();
  Grid.driver().findElement(By.id("SendMoney")).click();
}

@Test(groups = {"sessionSharingWebTests"},dependsOnMethods="testSendMoneyFlow1")
@WebTest(sessionName="login-flow",openNewSession=false)
public void testSendMoneyFlow2() {
  //Will open up in the same browser window using the same web driver session.
  Grid.open("http://www.paypal.com/paymentFlow2");
  Grid.driver().findElement(By.id("Receiver")).click();
  Grid.driver().findElement(By.id("SendMoney")).click();
}