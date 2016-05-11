// since this test is going to be used by both android and ios,
//    @MobileTest parameters are defined in test suite.
@Test
@MobileTest
public void testButtonOnApp() {
  // new page can be generated with different parameters.
  // locale is the locale that the test should be run with.
  // platform is mobile platform to use. it can be ANDROID or IOS.
  // if any parameter is not defined it will use the default one.
  //    1) new NativeAppTestPage(locale, platform)
  //    2) new NativeAppTestPage(locale, platform)
  //    3) new NativeAppTestPage(locale, platform)
  //    4) new NativeAppTestPage(locale, platform)
  NativeAppTestPage samplePage = new NativeAppTestPage();

  // Navigating to the short press button and click it
  samplePage.getSmallButton().click();

  // Once clicked check the result on the label
  assertTrue(samplePage.getSmallLabel().getValue() != null);
  SeLionReporter.log(samplePage.getSmallLabel().getValue(), true);
}