@MobileTest(appName = "InternationalMountains")
@Test
public void testButtonOnApp() {
        // We are now creating a page object that represents the actual test page on the IOS App.
        // In our yaml file which resides under src/main/resources/GUIData our localization value is being
        // given as "english". The default value that SeLion assumes would be "US". So we have two options
        // 1. We instantiate the page by passing in the value of 'US' which is how we are going to be
        // dealing with our localizations.
        // 2. We can set this at the entire JVM level by passing in the value via the JVM argument
        // -DSELION_SITE_LOCALE=<locale_value>
        // 3. We can set this at a specific <test> level by setting the parameter
        // <parameter name="siteLocale" value="locale_value_to_be_set"/> in the suite xml file.
        NativeAppTestPage samplePage = new NativeAppTestPage("english");

        // Navigating to the Mountain List page to click on the first Mountain
        samplePage.getSampleUIATableCell().click();

        // Once landed on the Mountain 1 page, reading the text
        assertTrue(samplePage.getSampleUIAStaticText().getAttribute("name")!=null);
        MobileReporter.log(samplePage.getSampleUIAStaticText().getAttribute("name"), true);
    }

}
