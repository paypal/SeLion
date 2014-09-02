public class WalletPage extends BasicPageImpl {
  private BankContainer    bankContainer;
  private Label            bankHeaderLabel;

   /**
   * SeLion GUI Html Object Standard
   *
   * The naming conventions followed in SeLion for html elements are as follows,
   *
   * <alias>{corresponding class name of the html element in SeLion}
   *   where - <alias> will be the object name with its first letter in lowercase.
   *
   *
   * <alias>Button                For Button html element.
   * <alias>CheckBox              For Check Box html element.
   * <alias>DatePicker            For Date Picker html element.
   * <alias>Form                  For Form html element.
   * <alias>Image                 For Image html element.
   * <alias>Label                 For Label html element.
   * <alias>Link                  For Link html element.
   * <alias>RadioButton           For Radio Button html element.
   * <alias>List                  For Select List html element.
   * <alias>Table                 For Table html element.
   * <alias>TextField             For Text Field html element.
   * <alias>Container             For Container html element.
   *
   */

  private static String CLASS_NAME = "WalletPage";
  private static String PAGE_DOMAIN = "selion";

  /**
   * Creates a new WalletPage object
   */
  public WalletPage() {
    super.initPage(PAGE_DOMAIN, CLASS_NAME);
  }

  /**
   * Creates a new WalletPage object
   * @param siteLocale
   *         The Country locale for the site you are accessing
   */
  public WalletPage(String siteLocale) {
    super.initPage(PAGE_DOMAIN, CLASS_NAME, siteLocale);
  }

  public WalletPage getPage() {
    if (!isInitialized()) {
      loadObjectMap();
      initializeHtmlObjects(this, this.objectMap);
      //Initialize non SeLion html members here
    }
    return this;
  }

  /**
   * For Container : bankContainer
   */

  /**
   * Used to get the BankContainer.
   * @return bankContainer
   */
  public BankContainer getBankContainer() {
    return getPage().bankContainer;
  }

  /**
   * Used to get BankContainer at specified index.
   * @return bankContainer at index
   */
  public BankContainer getBankContainer(int index) {
    getPage().bankContainer.setIndex(index);
    return bankContainer;
  }

  /**
   * For Label : bankHeaderLabel
   */

  /**
   * Used to get bankHeaderLabel in the page WalletPage
   * @return bankHeaderLabel
   */
  public Label getBankHeaderLabel() {
    return getPage().bankHeaderLabel;
  }

  /**
   * Used to check for the specific text available in the control bankHeaderLabel
   */
  public boolean isTextPresentForBankHeaderLabel(String pattern) {
    return getPage().bankHeaderLabel.isTextPresent(pattern);
  }

  public class BankContainer extends Container {
    private Label            bankLast4DigitsLabel;
    private Label            bankTypeLabel;
    private Label            bankNameLabel;
    private Link             bankLink;

    /**
     * BankContainer Construction method <br>
     * <br>
     * <b>Usage:</b>
     *
     * private WalletPage pageInstance = new WalletPage();
     * private WalletPage.BankContainer bankContainer =
     *    pageInstance.new BankContainer("//span[@id='containerLocator']");
     *
     * @param locator
     *            the element locator
     */
    public BankContainer(String locator) {
      super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes.
     * Default controlName would be the element locator.
     *
     * @param locator
     *           the element locator
     * @param controlName
     *            the control name used for logging
     */
    public BankContainer(String locator, String controlName) {
      super(locator, controlName);
    }

    private BankContainer getContainer() {
      if (!isInitialized()) {
          loadObjectMap();
          initializeHtmlObjects(this, WalletPage.this.objectMap);
      }
      return this;
    }

    /**
     *
     * For BankContainer Label : bankLast4DigitsLabel
     *
     * Used to get bankLast4DigitsLabel in bankContainer
     * @return bankLast4DigitsLabel
     */
    public Label getBankLast4DigitsLabel() {
      return getContainer().bankLast4DigitsLabel;
    }

    /**
     *
     * For BankContainer Label : bankTypeLabel
     *
     * Used to get bankTypeLabel in bankContainer
     * @return bankTypeLabel
     */
    public Label getBankTypeLabel() {
      return getContainer().bankTypeLabel;
    }

    /**
     *
     * For BankContainer Label : bankNameLabel
     *
     * Used to get bankNameLabel in bankContainer
     * @return bankNameLabel
     */
    public Label getBankNameLabel() {
      return getContainer().bankNameLabel;
    }

    /**
     *
     * For BankContainer Link : bankLink
     *
     * Used to get bankLink in bankContainer
     * @return bankLink
     */
    public Link getBankLink() {
      return getContainer().bankLink;
    }
  }
}