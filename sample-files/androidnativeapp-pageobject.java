@MobileTest(appName = "com.paypal.selion.pageobjectsdemoapp:1.0", 
  device = "android:19")
@Test
public void testButtonOnApp() {
  // We are now creating a page object that represents the actual test page on 
  // the Android App.
  // In src/main/resources/GUIData/NativeAppTestPage.yaml, we have 
  // localization values for both 'FR' and 'US' with 'US' as the default 
  // locale for SeLion.
  // We have the following options to use locators for 'FR':
  // 1. We can instantiate the page by passing in the value of 'FR' which is 
  // how we are going to be dealing with our localizations.
  // 2. We can set this at the entire JVM level by passing in the value via 
  // the JVM argument -DSELION_SITE_LOCALE=FR
  // 3. We can set this at a specific <test> level by setting the parameter
  // <parameter name="siteLocale" value="FR"/> in the suite xml file.
  NativeAppTestPage samplePage = new NativeAppTestPage("FR");

  // Navigating to the short press button and click it
  samplePage.getShortPressButtonUiObject().click();

  // Once clicked check the result on the label
  assertTrue(samplePage.getShortPressLabelUiObject().getText() != null);
  SeLionReporter.log(samplePage.getShortPressLabelUiObject().getText(), true);
}