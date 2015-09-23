@WebTest
@Test(singleThreaded = true)
public class SessionSharingTest {
   @Test(groups = {"sessionSharingWebTests"}, priority = 1)
   public void testLoginFlow() {
     // Session is automatically created before this @Test method.
     Grid.open("http://www.paypal.com");
     // Enter the user credentials.
     Grid.driver().findElement(By.id("Email")).sendKeys(user.getUserName());
     Grid.driver().findElement(By.id("Password")).sendKeys(user.getPassword());
     Grid.driver().findElement(By.id("SignIn")).click();
   }

   @Test(groups = {"sessionSharingWebTests"}, priority = 2)
   public void testSendMoneyFlow1() {
     // Opens URL in the same browser window using the same WebDriver session.
     Grid.open("http://www.paypal.com/paymentFlow1");
     Grid.driver().findElement(By.id("Receiver")).click();
     Grid.driver().findElement(By.id("SendMoney")).click();
   }

   @Test(groups = {"sessionSharingWebTests"}, priority = 3)
   public void testSendMoneyFlow2() {
     // Opens URL in the same browser window using the same WebDriver session.
     Grid.open("http://www.paypal.com/paymentFlow2");
     Grid.driver().findElement(By.id("Receiver")).click();
     Grid.driver().findElement(By.id("SendMoney")).click();
     // Session is automatically closed after this @Test method.
   }
}
