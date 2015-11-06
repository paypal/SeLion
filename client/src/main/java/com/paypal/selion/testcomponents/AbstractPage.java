/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014-15 PayPal                                                                                       |
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

package com.paypal.selion.testcomponents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang.StringUtils;

import com.paypal.selion.configuration.Config.ConfigProperty;
import com.paypal.selion.configuration.ConfigManager;
import com.paypal.selion.configuration.LocalConfig;
import com.paypal.selion.internal.platform.grid.AbstractTestSession;
import com.paypal.selion.internal.platform.pageyaml.GuiMapReader;
import com.paypal.selion.internal.platform.pageyaml.GuiMapReaderFactory;
import com.paypal.selion.platform.grid.Grid;
import com.paypal.selion.platform.html.WebPage;

public abstract class AbstractPage implements WebPage {

    // Initialization state of WebPage
    /** The page initialized. */
    private boolean pageInitialized;
    // Object map queue for loading
    /** The map queue. */
    private final Queue<String[]> mapQueue;
    // used to determine our locale (e.g. US, UK, DE, etc.)
    /** The site. */
    private String site;
    /** The page title. */
    private String pageTitle;

    /** Map to store our GUI object map content. */
    private Map<String, String> objectMap;

    /** The UNKNOWN_PAGE_TITLE. */
    private static final String UNKNOWN_PAGE_TITLE = "unknown-title";

    /** The elements that should be present on the Page **/
    private final List<String> pageValidators = new ArrayList<String>();

    /** Map to store our GUI object map content for all Containers */
    private final Map<String, Map<String, String>> objectContainerMap = new HashMap<String, Map<String, String>>();

    protected void setPageInitialized(boolean pageInitialized) {
        this.pageInitialized = pageInitialized;
    }

    protected String getPageTitle() {
        return pageTitle;
    }

    protected void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

    protected List<String> getPageValidators() {
        return pageValidators;
    }

    protected Map<String, Map<String, String>> getObjectContainerMap() {
        return objectContainerMap;
    }

    protected AbstractPage() {
        pageTitle = UNKNOWN_PAGE_TITLE;
        mapQueue = new LinkedList<String[]>();
        site = ConfigProperty.SITE_LOCALE.getDefaultValue();
        pageInitialized = false;

    }

    public void initPage(String pageDomain, String pageClassName) {
        // add the page domain and class name to the load queue
        mapQueue.add(new String[] { pageDomain, pageClassName });
        AbstractTestSession session = Grid.getTestSession();

        if (session != null && StringUtils.isNotBlank(session.getXmlTestName())) {
            LocalConfig lc = ConfigManager.getConfig(session.getXmlTestName());
            site = lc.getConfigProperty(ConfigProperty.SITE_LOCALE);
        }
    }

    /**
     * Load object map.
     */
    protected Map<String, String> getObjectMap() {
        if (isInitialized())
            return objectMap;
        while (mapQueue.size() > 0) {
            String[] map = mapQueue.poll();
            String pageDomain = map[0];
            String pageClassName = map[1];
            Map<String, String> currentObjectMap;
            try {

                GuiMapReader dataProvider = GuiMapReaderFactory.getInstance(pageDomain, pageClassName);
                currentObjectMap = dataProvider.getGuiMap(site);

                pageTitle = currentObjectMap.get("pageTitle");

                for (String key : currentObjectMap.keySet()) {
                    if (key.endsWith("Container")) {
                        objectContainerMap.put(key, dataProvider.getGuiMapForContainer(key, site));
                    }
                }

                pageValidators.addAll(dataProvider.getPageValidators());

                if (objectMap != null) {
                    objectMap.putAll(currentObjectMap);
                } else {
                    objectMap = currentObjectMap;
                }
            } catch (Exception e) {
                throw new RuntimeException("Unable to initialize page data for " + pageDomain + "/" + pageClassName
                        + ". Root cause:" + e, e); // NOSONAR
            }
        }
        pageInitialized = true;
        return objectMap;
    }

    /**
     * Load object map. This method takes a HashMap<String, String> and uses it to populate the objectMap This is
     * intended to allow for the use of programmatically generated locators in addition to the yaml file format IDs and
     * Locators
     * 
     * @param sourceMap
     *            the source map
     */
    protected void loadObjectMap(HashMap<String, String> sourceMap) {

        if (sourceMap == null) {
            return;
        }
        
        if(sourceMap.isEmpty()){
            return;
        }
        
        if (sourceMap.containsKey("pageTitle")) {
            pageTitle = sourceMap.get("pageTitle");
        }
        if (objectMap == null) {
            objectMap = new HashMap<String, String>();
        }
        objectMap.putAll(sourceMap);

        pageInitialized = true;
    }

    public void initPage(String pageDomain, String pageClassName, String siteLocale) {
        initPage(pageDomain, pageClassName);
        site = siteLocale;
    }

    public boolean isInitialized() {
        return pageInitialized;
    }

    public String getExpectedPageTitle() {
        throw new UnsupportedOperationException("This operation is NOT supported.");
    }

    public String getSiteLocale() {
        return site;
    }

    public void validatePage() {
        throw new UnsupportedOperationException("This operation is NOT supported.");
    }

    public boolean isPageValidated() {
        throw new UnsupportedOperationException("This operation is NOT supported.");
    }

}
