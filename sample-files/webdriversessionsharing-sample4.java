@WebTest
@Test(singleThreaded = true)
public class SessionSharingTest {
   @BeforeClass
   public void openSite() {
     // Session is automatically created before this @BeforeClass method.
     Grid.open("http://www.paypal.com");
   }

   @Test(groups = {"sessionSharingWebTests"}, priority = 1)
   public void testLoginFlow() {
     // Enter the user credentials.
     Grid.driver().findElement(By.id("Email")).sendKeys(user.getUserName());
     Grid.driver().findElement(By.id("Password")).sendKeys(user.getPassword());
     Grid.driver().findElement(By.id("SignIn")).click();
   }

   @Test(groups = {"sessionSharingWebTests"}, priority = 2)
   public void testSendMoneyFlow1() {
     // Opens URL in the same browser window using the same web driver session.
     Grid.open("http://www.paypal.com/paymentFlow1");
     Grid.driver().findElement(By.id("Receiver")).click();
     Grid.driver().findElement(By.id("SendMoney")).click();
     // Session is automatically closed after this @Test method, when @AfterClass 
     // is not present.
   }

/**
   If specified, session is automatically closed after this @AfterClass instead.   
   @AfterClass
   public void after() {
     // do something
   }
*/   
}
