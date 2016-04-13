package com.paypal.selion.mobile.sample;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.MobileImplementationFinder;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileElement;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class SampleMobilePage extends BasicPageImpl {

    private MobileButton sampleButton;
    private MobileElement sampleElement;
    private MobileTextField sampleTextField;

    private static String CLASS_NAME = "SampleMobilePage";
    private static String PAGE_DOMAIN = "paypal";


    /**
     * Creates a new SampleMobilePage object
     */
    public SampleMobilePage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     */
    public SampleMobilePage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform The Platform for the site you are accessing
     */
    public SampleMobilePage(WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, platform);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     * @param platform   The Platform for the site you are accessing
     */
    public SampleMobilePage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public SampleMobilePage getPage() {
        return this;
    }


    /**
     * Used to get sampleButton in the page SampleMobilePage
     *
     * @return sampleButton
     */
    public MobileButton getSampleButton() {
        MobileButton element = this.sampleButton;
        if (element == null) {
            this.sampleButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton.class, getObjectMap().get("sampleButton"));
        }
        return this.sampleButton;
    }


    /**
     * Used to get sampleElement in the page SampleMobilePage
     *
     * @return sampleElement
     */
    public MobileElement getSampleElement() {
        MobileElement element = this.sampleElement;
        if (element == null) {
            this.sampleElement = MobileImplementationFinder.instantiate(getPlatform(), MobileElement.class, getObjectMap().get("sampleElement"));
        }
        return this.sampleElement;
    }


    /**
     * Used to get sampleTextField in the page SampleMobilePage
     *
     * @return sampleTextField
     */
    public MobileTextField getSampleTextField() {
        MobileTextField element = this.sampleTextField;
        if (element == null) {
            this.sampleTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField.class, getObjectMap().get("sampleTextField"));
        }
        return this.sampleTextField;
    }

}
