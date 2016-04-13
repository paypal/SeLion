package com.paypal.selion.mobile.sample.mobile;

import com.paypal.selion.internal.platform.grid.WebDriverPlatform;
import com.paypal.selion.platform.mobile.MobileImplementationFinder;
import com.paypal.selion.platform.mobile.elements.MobileButton;
import com.paypal.selion.platform.mobile.elements.MobileTextField;
import com.paypal.selion.testcomponents.BasicPageImpl;

public class TouchPage extends BasicPageImpl {

    private MobileButton nextButton;
    private MobileButton touchButton;
    private MobileTextField touchTextField;
    private MobileTextField topTextField;
    private MobileButton preButton;

    private static String CLASS_NAME = "TouchPage";
    private static String PAGE_DOMAIN = "paypal/mobile";


    /**
     * Creates a new TouchPage object
     */
    public TouchPage() {
        super.initPage(PAGE_DOMAIN, CLASS_NAME);
    }
    
    /**
     * Creates a new TouchPage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     */
    public TouchPage(String siteLocale) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param platform The Platform for the site you are accessing
     */
    public TouchPage(WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, platform);
    }

    /**
     * Creates a new SampleMobilePage object
     *
     * @param siteLocale The Country locale for the site you are accessing
     * @param platform   The Platform for the site you are accessing
     */
    public TouchPage(String siteLocale, WebDriverPlatform platform) {
        super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale, platform);
    }

    public TouchPage getPage() {
        return this;
    }


    /**
     * Used to get nextButton in the page TouchPage
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
     * Used to get touchButton in the page TouchPage
     *
     * @return touchButton
     */
    public MobileButton getTouchButton() {
        MobileButton element = this.touchButton;
        if (element == null) {
            this.touchButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton .class, getObjectMap().get("touchButton"));
        }
        return this.touchButton;
    }


    /**
     * Used to get touchTextField in the page TouchPage
     *
     * @return touchTextField
     */
    public MobileTextField getTouchTextField() {
        MobileTextField element = this.touchTextField;
        if (element == null) {
            this.touchTextField = MobileImplementationFinder.instantiate(getPlatform(), MobileTextField .class, getObjectMap().get("touchTextField"));
        }
        return this.touchTextField;
    }


    /**
     * Used to get topTextField in the page TouchPage
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
     * Used to get preButton in the page TouchPage
     *
     * @return preButton
     */
    public MobileButton getPreButton() {
        MobileButton element = this.preButton;
        if (element == null) {
            this.preButton = MobileImplementationFinder.instantiate(getPlatform(), MobileButton .class, getObjectMap().get("preButton"));
        }
        return this.preButton;
    }

}
