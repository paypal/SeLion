package com.paypal.selion.mobile.sample.mobile;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.MobileImplementationFinder;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class SamplePage extends BasicPageImpl {

    private MobileButton nextButton;
    private MobileTextField topTextField;
    private MobileTextField mainTextField;

    private static String CLASS_NAME = "SamplePage";
    private static String PAGE_DOMAIN = "paypal/mobile";


    /**
     * Creates a new SamplePage object
     */
    public SamplePage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }
    
    /**
     * Creates a new SamplePage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     */
    public SamplePage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform The Platform for the site you are accessing
     */
    public SamplePage(WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, platform);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     * @param platform   The Platform for the site you are accessing
     */
    public SamplePage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public SamplePage getPage() {
        return this;
    }


    /**
     * Used to get nextButton in the page SamplePage
     *
     * @return nextButton
     */
    public MobileButton getNextButton() {
        MobileButton element = this.nextButton;
        if (element == null) {
            this.nextButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton .class, getObjectMap().get("nextButton"));
        }
        return this.nextButton;
    }


    /**
     * Used to get topTextField in the page SamplePage
     *
     * @return topTextField
     */
    public MobileTextField getTopTextField() {
        MobileTextField element = this.topTextField;
        if (element == null) {
            this.topTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField .class, getObjectMap().get("topTextField"));
        }
        return this.topTextField;
    }


    /**
     * Used to get mainTextField in the page SamplePage
     *
     * @return mainTextField
     */
    public MobileTextField getMainTextField() {
        MobileTextField element = this.mainTextField;
        if (element == null) {
            this.mainTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField .class, getObjectMap().get("mainTextField"));
        }
        return this.mainTextField;
    }

}
