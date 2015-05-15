@MobileTest(appName = "InternationalMountains", device = "iphone:7.1", 
  deviceType = "iPhone4s")
@Test
public void testButtonOnApp() {
  // We are now creating a page object that represents the actual test page on 
  // the iOS App.
  // In src/main/resources/GUIData/NativeAppTestPage.yaml, we have localization 
  // values for both 'FR' and 'US' with 'US' as the default locale for SeLion.
  // We have the following options to use locators for 'FR':
  // 1. We can instantiate the page by passing in the value of 'FR' which is how 
  // we are going to be dealing with our localizations.
  // 2. We can set this at the entire JVM level by passing in the value via the 
  // JVM argument -DSELION_SITE_LOCALE=FR
  // 3. We can set this at a specific <test> level by setting the parameter
  // <parameter name="siteLocale" value="FR"/> in the suite xml file.
  NativeAppTestPage samplePage = new NativeAppTestPage("FR");

  // Navigating to the Mountain List page to click on the first Mountain
  samplePage.getSampleUIATableCell().click();

  // Once landed on the Mountain 1 page, reading the text
  assertTrue(samplePage.getSampleUIAStaticText().getAttribute("name")!=null);
  SeLionReporter.log(samplePage.getSampleUIAStaticText().getAttribute("name"), 
    true);
}