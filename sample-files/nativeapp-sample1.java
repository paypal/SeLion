@MobileTest(appName = "InternationalMountains")
@Test
public void testMethod() throws InterruptedException {
  SeLionReporter.log("My Screenshot 1", true);
  List<WebElement> cells = Grid.iOSDriver().
    findElements(By.className("UIATableCell"));
  Assert.assertEquals(9, cells.size());

  // get the 1st mountain
  WebElement first = cells.get(0);
  first.click();
  Thread.sleep(10 * 1000);

  // take a screenshot using the normal selenium api.
  SeLionReporter.log("My Screenshot 2", true);

  // access the content
  By selector = By.xpath("//UIAStaticText[contains(@name,'climbed')]");
  WebElement text = Grid.iOSDriver().findElement(selector);
  System.out.println(text.getAttribute("name"));
}