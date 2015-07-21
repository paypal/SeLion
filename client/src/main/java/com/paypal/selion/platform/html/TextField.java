/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 PayPal                                                                                          |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.platform.html;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebElement;

import com.google.common.base.Preconditions;
import com.paypal.selion.configuration.Config;
import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.html.support.events.Typeable;

/**
 * This class is the web element Input wrapper.
 * <p>
 * In this class, the method 'type' is encapsulated and invokes a SeLion session to type content to the specified
 * element.
 * </p>
 * 
 */
public class TextField extends AbstractElement implements Typeable {

    /**
     * TextField Construction method <br>
     * <br>
     * <b>Usage:</b>
     * 
     * <pre>
     * private TextField txtTitle = new TextField("//input[@id='title']")
     * </pre>
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * 
     */
    public TextField(String locator) {
        super(locator);
    }

    /**
     * Use this constructor to override default controlName for logging purposes. Default controlName would be the
     * element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     */
    public TextField(String locator, String controlName) {
        super(locator, controlName);
    }

    /**
     * Use this constructor to create a TextField contained within a parent.
     * 
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     */
    public TextField(ParentTraits parent, String locator) {
        super(parent, locator);
    }

    /**
     * Use this constructor to create a TextField contained within a parent. This constructor will also override default
     * controlName for logging purposes. Default controlName would be the element locator.
     * 
     * @param locator
     *            - A String that represents the means to locate this element (could be id/name/xpath/css locator).
     * @param controlName
     *            the control name used for logging
     * @param parent
     *            - A {@link ParentTraits} object that represents the parent element for this element.
     * 
     */
    public TextField(String locator, String controlName, ParentTraits parent) {
        super(locator, controlName, parent);
    }

    /**
     * The TextField type function
     * 
     * It invokes SeLion session to handle the type action against the element.
     */
    public void type(String value) {
        getDispatcher().beforeType(this, value);
        
        RemoteWebElement element = getElement();
        element.clear();
        element.sendKeys(value);
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIActions(UIActions.ENTERED, value);
        }
        
        getDispatcher().afterType(this, value);
    }

    /**
     * The TextField type function which allow users to keep the TextField and append the input text to it.
     * 
     * It invokes SeLion session to handle the type action against the element.
     */
    public void type(String value, boolean isKeepExistingText) {
        if (isKeepExistingText) {
            getDispatcher().beforeType(this, value);
            
            getElement().sendKeys(value);
            if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
                logUIActions(UIActions.ENTERED, value);
            }
            
            getDispatcher().afterType(this, value);
        } else {
            type(value);
        }
    }

    /**
     * Text TextField clear function
     * 
     * To clear the text box.
     */
    public void clear() {
        getElement().clear();
        if (Config.getBoolConfigProperty(ConfigProperty.ENABLE_GUI_LOGGING)) {
            logUIAction(UIActions.CLEARED);
        }
    }

    /**
     * The TextField isEditable function
     * 
     * It invokes SeLion session to handle the isEditable function against the element.
     */
    public boolean isEditable() {
        return ((RemoteWebElement) getElement()).isEnabled();
    }

    /**
     * Get the text value from a TextField object.
     * 
     * @return text is the text in the TextField box.
     */
    public String getText() {
        String text = getElement().getText();
        if (text.isEmpty()) {
            text = getValue();
        }
        return text;
    }

    /**
     * A Utility method that helps with uploading a File to a Web Application.
     * 
     * @param filePath
     *            - A String that represents the file name to be uploaded along with its path.
     * <pre>
     * TextField txtBox = new TextField(&quot;upfile&quot;);
     * // Here the html snippet for the above text box would be
     * // {@literal &lt;}input type=file name=upfile{@literal &gt;}
     * // Make sure you are providing the locator of the &quot;file&quot; type textbox
     * // else uploadFile() method will fail.
     * txtBox.uploadFile(&quot;src/test/resources/upload.txt&quot;);
     * Button submitButton = new Button(&quot;//input[@value='Press']&quot;);
     * submitButton.click();
     *</pre>
     */
    public void uploadFile(String filePath) {
        SeLionLogger.getLogger().entering(filePath);
        Preconditions.checkArgument(StringUtils.isNotBlank(filePath), "Please provide a valid file path to work with.");
        String filePathToUse = new File(filePath).getAbsolutePath();
        LocalFileDetector detector = new LocalFileDetector();
        RemoteWebElement element = getElement();
        element.setFileDetector(detector);
        element.sendKeys(filePathToUse);
        SeLionLogger.getLogger().exiting();
    }

}
