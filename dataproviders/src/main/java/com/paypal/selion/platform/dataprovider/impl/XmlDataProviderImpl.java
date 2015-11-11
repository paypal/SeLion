/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 PayPal                                                                                          |
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

package com.paypal.selion.platform.dataprovider.impl;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.DataProviderException;
import com.paypal.selion.platform.dataprovider.DataResource;
import com.paypal.selion.platform.dataprovider.XmlDataProvider;
import com.paypal.selion.platform.dataprovider.XmlDataSource;
import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;
import com.paypal.selion.platform.dataprovider.filter.SimpleIndexInclusionFilter;
import com.paypal.selion.platform.dataprovider.pojos.KeyValueMap;
import com.paypal.selion.platform.dataprovider.pojos.KeyValuePair;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class provides several methods to retrieve test data from XML files. Users can get data returned in an Object 2D
 * array by loading the XML file. If the entire XML file is not needed then specific data entries can be retrieved by
 * indexes.
 *
 */
public final class XmlDataProviderImpl implements XmlDataProvider {

    private static SimpleLogger logger = SeLionLogger.getLogger();
    private final XmlDataSource resource;

    public XmlDataProviderImpl(XmlDataSource resource) {
        this.resource = resource;
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data.
     *
     * @return A two dimensional object array.
     */
    @Override
    public Object[][] getAllData() {
        logger.entering();
        Object[][] objectArray;

        if ((null == resource.getCls()) && (null != resource.getXpathMap())) {
            Document doc = getDocument();
            Object[][][] multipleObjectDataProviders = new Object[resource.getXpathMap().size()][][];
            int i = 0;
            for (Entry<String, Class<?>> entry : resource.getXpathMap().entrySet()) {
                String xml = getFilteredXml(doc, entry.getKey());
                List<?> object = loadDataFromXml(xml, entry.getValue());
                Object[][] objectDataProvider = DataProviderHelper.convertToObjectArray(object);
                multipleObjectDataProviders[i++] = objectDataProvider;
            }
            objectArray = DataProviderHelper.getAllDataMultipleArgs(multipleObjectDataProviders);
        } else {
            List<?> objectList = loadDataFromXmlFile();
            objectArray = DataProviderHelper.convertToObjectArray(objectList);
        }

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return objectArray;
    }

    /**
     * Generates an object array in iterator as TestNG DataProvider from the XML data filtered per {@code dataFilter}.
     *
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     */
    @Override
    public Iterator<Object[]> getDataByFilter(DataProviderFilter dataFilter) {
        logger.entering(new Object[] { resource, dataFilter });
        List<Object[]> allObjs = getDataListByFilter(dataFilter);
        return allObjs.iterator();
    }

    /**
     * Generates an objects in List from the XML data filtered per {@code dataFilter}.
     *
     * @param dataFilter an implementation class of {@link DataProviderFilter}
     * @return List of objects
     */
    private List<Object[]> getDataListByFilter(DataProviderFilter dataFilter) {

        logger.entering(dataFilter);
        List<Object[]> allObjs = new ArrayList<>();
        if ((null == resource.getCls()) && (null != resource.getXpathMap())) {
            Document doc = getDocument();
            for (Entry<String, Class<?>> entry : resource.getXpathMap().entrySet()) {
                String xml = getFilteredXml(doc, entry.getKey());
                List<?> objectList = loadDataFromXml(xml, entry.getValue());
                List<Object[]> singleResourceObjs = DataProviderHelper.filterToListOfObjects(objectList, dataFilter);
                allObjs.addAll(singleResourceObjs);
            }
        } else {
            List<?> objectList = loadDataFromXmlFile();
            allObjs = DataProviderHelper.filterToListOfObjects(objectList, dataFilter);
        }
        logger.exiting(allObjs);

        return allObjs;
    }

    /**
     * Generates an object array in iterator as TestNG DataProvider from the XML data filtered per given indexes string.
     * This method may throw {@link DataProviderException} when an unexpected error occurs during data provision from
     * XML file.
     *
     * @param filterIndexes
     *            The indexes for which data is to be fetched as a conforming string pattern.
     *
     * @return An Object[][] object to be used with TestNG DataProvider.
     */
    @Override
    public Object[][] getDataByIndex(String filterIndexes) {
        logger.entering(filterIndexes);

        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter(filterIndexes);
        List<Object[]> objectList = getDataListByFilter(filter);
        Object[][] objectArray = DataProviderHelper.convertToObjectArray(objectList);

        logger.exiting((Object[]) objectArray);
        return objectArray;
    }

    /**
     * Generates an object array in iterator as TestNG DataProvider from the XML data filtered per given indexes. This
     * method may throw {@link DataProviderException} when an unexpected error occurs during data provision from XML
     * file.
     *
     * @param indexes
     *            The indexes for which data is to be fetched as a conforming string pattern.
     *
     * @return An Object[][] object to be used with TestNG DataProvider.
     */
    @Override
    public Object[][] getDataByIndex(int[] indexes) {
        logger.entering(indexes);

        SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter(indexes);
        List<Object[]> objectList = getDataListByFilter(filter);
        Object[][] objectArray = DataProviderHelper.convertToObjectArray(objectList);

        logger.exiting((Object[]) objectArray);
        return objectArray;
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data representing a map of name value
     * collection.
     *
     * This method needs the referenced {@link DataResource} to be instantiated using its constructors with
     * parameter {@code Class<?> cls} and set to {@code KeyValueMap.class}. The implementation in this method is tightly
     * coupled with {@link KeyValueMap} and {@link KeyValuePair}.
     *
     * The hierarchy and name of the nodes are strictly as instructed. A name value pair should be represented as nodes
     * 'key' and 'value' as child nodes contained in a parent node named 'item'. A sample data with proper tag names is
     * shown here as an example:
     *
     * <pre>
     * <items>
     *     <item>
     *         <key>k1</key>
     *         <value>val1</value>
     *     </item>
     *     <item>
     *         <key>k2</key>
     *         <value>val2</value>
     *     </item>
     *     <item>
     *         <key>k3</key>
     *         <value>val3</value>
     *     </item>
     * </items>
     * </pre>
     *
     * @return A two dimensional object array.
     */
    @Override
    public Object[][] getAllKeyValueData() {
        logger.entering();

        Object[][] objectArray;
        try {
            JAXBContext context = JAXBContext.newInstance(resource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(resource.getInputStream());
            Map<String, KeyValuePair> keyValueItems = unmarshaller
                    .unmarshal(xmlStreamSource, KeyValueMap.class).getValue().getMap();
            objectArray = DataProviderHelper.convertToObjectArray(keyValueItems);
        } catch (JAXBException excp) {
            throw new DataProviderException("Error unmarshalling XML file.", excp);
        }

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return objectArray;
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data representing a map of name value
     * collection filtered by keys.
     *
     * A name value item should use the node name 'item' and a specific child structure since the implementation depends
     * on {@link KeyValuePair} class. The structure of an item in collection is shown below where 'key' and 'value' are
     * child nodes contained in a parent node named 'item':
     *
     * <pre>
     * <items>
     *     <item>
     *         <key>k1</key>
     *         <value>val1</value>
     *     </item>
     *     <item>
     *         <key>k2</key>
     *         <value>val2</value>
     *     </item>
     *     <item>
     *         <key>k3</key>
     *         <value>val3</value>
     *     </item>
     * </items>
     * </pre>
     *
     * @param keys
     *            The string keys to filter the data.
     * @return A two dimensional object array.
     */
    @Override
    public Object[][] getDataByKeys(String[] keys) {
        logger.entering(Arrays.toString(keys));
        if (null == resource.getCls()) {
            resource.setCls(KeyValueMap.class);
        }

        Object[][] objectArray;
        try {
            JAXBContext context = JAXBContext.newInstance(resource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(resource.getInputStream());
            Map<String, KeyValuePair> keyValueItems = unmarshaller
                    .unmarshal(xmlStreamSource, KeyValueMap.class).getValue().getMap();
            objectArray = DataProviderHelper.getDataByKeys(keyValueItems, keys);
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new DataProviderException("Error unmarshalling XML file.", excp);
        }

        // Passing no arguments to exiting() because implementation to print 2D array could be highly recursive.
        logger.exiting();
        return objectArray;
    }

    /**
     * Gets xml data and returns in a hashtable instead of an Object 2D array. Only compatible with a xml file
     * formatted to return a map. <br>
     * <br>
     * XML file example:
     *
     * <pre>
     * <items>
     *     <item>
     *         <key>k1</key>
     *         <value>val1</value>
     *     </item>
     *     <item>
     *         <key>k2</key>
     *         <value>val2</value>
     *     </item>
     *     <item>
     *         <key>k3</key>
     *         <value>val3</value>
     *     </item>
     * </items>
     * </pre>
     *
     * @return xml data in form of a Hashtable.
     */
    @Override
    public Hashtable<String, Object> getDataAsHashtable() {
        logger.entering();
        if (null == resource.getCls()) {
            resource.setCls(KeyValueMap.class);
        }

        Hashtable<String, Object> dataHashTable = new Hashtable<>();
        try {
            JAXBContext context = JAXBContext.newInstance(resource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(resource.getInputStream());
            Map<String, KeyValuePair> keyValueItems = unmarshaller
                    .unmarshal(xmlStreamSource, KeyValueMap.class).getValue().getMap();
            for (Entry<?, ?> entry : keyValueItems.entrySet()) {
                dataHashTable.put((String) entry.getKey(), entry.getValue());
            }
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new DataProviderException("Error unmarshalling XML file.", excp);
        }

        logger.exiting();
        return dataHashTable;
    }

    /**
     * Generates a list of the declared type after parsing the XML file.
     *
     * @return A {@link List} of object of declared type {@link XmlFileSystemResource#getCls()}.
     */
    private List<?> loadDataFromXmlFile() {
        logger.entering();
        Preconditions.checkArgument(resource.getCls() != null, "Please provide a valid type.");
        List<?> returned;

        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class, resource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(resource.getInputStream());
            Wrapper<?> wrapper = unmarshaller.unmarshal(xmlStreamSource, Wrapper.class).getValue();
            returned = wrapper.getList();
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new DataProviderException("Error unmarshalling XML file.", excp);
        }

        logger.exiting(returned);
        return returned;
    }

    /**
     * Generates a list of the declared type after parsing the XML data string.
     *
     * @param xml
     *            String containing the XML data.
     * @param cls
     *            The declared type modeled by the XML content.
     * @return A {@link List} of object of declared type {@link XmlFileSystemResource#getCls()}.
     */
    private List<?> loadDataFromXml(String xml, Class<?> cls) {
        logger.entering(new Object[] { xml, cls });
        Preconditions.checkArgument(cls != null, "Please provide a valid type.");
        List<?> returned;

        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class, cls);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader xmlStringReader = new StringReader(xml);
            StreamSource streamSource = new StreamSource(xmlStringReader);
            Wrapper<?> wrapper = unmarshaller.unmarshal(streamSource, Wrapper.class).getValue();
            returned = wrapper.getList();
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new DataProviderException("Error unmarshalling XML string.", excp);
        }

        logger.exiting(returned);
        return returned;
    }

    /**
     * Loads the XML data from the {@link XmlFileSystemResource} into a {@link org.dom4j.Document}.
     *
     * @return A Document object.
     */
    private Document getDocument() {
        logger.entering();
        DOMDocumentFactory domFactory = new DOMDocumentFactory();
        SAXReader reader = new SAXReader(domFactory);
        Document doc;

        try {
            doc = reader.read(resource.getInputStream());
        } catch (DocumentException excp) {
            logger.exiting(excp.getMessage());
            throw new DataProviderException("Error reading XML data.", excp);
        }

        logger.exiting(doc.asXML());
        return doc;
    }

    /**
     * Generates an XML string containing only the nodes filtered by the XPath expression.
     *
     * @param document
     *            An XML {@link org.dom4j.Document}
     * @param xpathExpression
     *            A string indicating the XPath expression to be evaluated.
     * @return A string of XML data with root node named "root".
     */
    @SuppressWarnings("unchecked")
    private String getFilteredXml(Document document, String xpathExpression) {
        logger.entering(new Object[] { document, xpathExpression });

        List<Node> nodes = (List<Node>) document.selectNodes(xpathExpression);
        StringBuilder newDocument = new StringBuilder(document.asXML().length());
        newDocument.append("<root>");
        for (Node n : nodes) {
            newDocument.append(n.asXML());
        }
        newDocument.append("</root>");

        logger.exiting(newDocument);
        return newDocument.toString();
    }
}