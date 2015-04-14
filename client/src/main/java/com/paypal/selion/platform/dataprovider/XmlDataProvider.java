/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2015 eBay Software Foundation                                                                        |
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

package com.paypal.selion.platform.dataprovider;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.jxpath.JXPathContext;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.dom.DOMDocumentFactory;
import org.dom4j.io.SAXReader;

import com.google.common.base.Preconditions;
import com.paypal.selion.logger.SeLionLogger;
import com.paypal.selion.platform.dataprovider.filter.DataProviderFilter;
import com.paypal.selion.platform.dataprovider.pojos.KeyValueMap;
import com.paypal.selion.platform.dataprovider.pojos.KeyValuePair;
import com.paypal.test.utilities.logging.SimpleLogger;

/**
 * This class provides several methods to retrieve test data from XML files. Users can get data returned in an Object 2D
 * array by loading the XML file. If the entire XML file is not needed then specific data entries can be retrieved by
 * indexes.
 * 
 */
public final class XmlDataProvider {

    private static SimpleLogger logger = SeLionLogger.getLogger();

    // Hiding constructor for class that contains only static methods
    private XmlDataProvider() {
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data.
     * 
     * @param xmlResource
     *            A {@link XmlFileSystemResource} object containing the XML file location and representing type.
     * @return A two dimensional object array.
     * @throws XmlDataProviderException
     *             when the XML content in file is invalid, or cannot be unmarshallesd to declared type.
     */
    public static Object[][] getAllData(XmlFileSystemResource xmlResource) throws XmlDataProviderException {
        logger.entering(xmlResource);
        Object[][] objectArray;

        if ((null == xmlResource.getCls()) && (null != xmlResource.getXpathMap())) {
            Document doc = getDocument(xmlResource);
            Object[][][] multipleObjectDataProviders = new Object[xmlResource.getXpathMap().size()][][];
            int i = 0;
            for (Entry<String, Class<?>> entry : xmlResource.getXpathMap().entrySet()) {
                String xml = getFilteredXml(doc, entry.getKey());
                List<?> object = loadDataFromXml(xml, entry.getValue());
                Object[][] objectDataProvider = DataProviderHelper.convertToObjectArray(object);
                multipleObjectDataProviders[i++] = objectDataProvider;
            }
            objectArray = DataProviderHelper.getAllDataMultipleArgs(multipleObjectDataProviders);
        } else {
            List<?> objectList = loadDataFromXmlFile(xmlResource);
            objectArray = DataProviderHelper.convertToObjectArray(objectList);
        }

        logger.exiting();
        return objectArray;
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data filtered per {@code dataFilter}.
     * 
     * @param resource
     *            A {@link FileSystemResource} that represents a data source.
     * @param dataFilter
     *            an implementation class of {@link DataProviderFilter}
     * @return An iterator over a collection of Object Array to be used with TestNG DataProvider
     * @throws DataProviderException
     * @throws IOException
     * @throws YamlDataProviderException
     */
    public static Iterator<Object[]> getDataByFilter(XmlFileSystemResource xmlResource, DataProviderFilter dataFilter)
            throws DataProviderException {
        logger.entering(new Object[] { xmlResource, dataFilter });
        List<Object[]> allObjs = new ArrayList<Object[]>();
        if ((null == xmlResource.getCls()) && (null != xmlResource.getXpathMap())) {
            Document doc = getDocument(xmlResource);
            for (Entry<String, Class<?>> entry : xmlResource.getXpathMap().entrySet()) {
                String xml = getFilteredXml(doc, entry.getKey());
                List<?> objectList = loadDataFromXml(xml, entry.getValue());
                List<Object[]> singleResourceObjs = DataProviderHelper.filterToListOfObjects(objectList, dataFilter);
                allObjs.addAll(singleResourceObjs);
            }
        } else {
            List<?> objectList = loadDataFromXmlFile(xmlResource);
            allObjs = DataProviderHelper.filterToListOfObjects(objectList, dataFilter);
        }
        return allObjs.iterator();
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data representing a map of name value
     * collection.
     * 
     * A name value item should use the node name 'item' and a specific child structure since the implementation depends
     * on {@link KeyValuePair} class. The structure of an item in collection is shown below where 'key' and 'value' are
     * child nodes contained in a parent node named 'item' :-
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
     * @param xmlResource
     *            A {@link XmlFileSystemResource} object containing the XML file location and representing type.
     * @return A two dimensional object array.
     * @throws XmlDataProviderException
     *             when the XML content in file is invalid, or cannot be unmarshalled to declared type.
     */
    public static Object[][] getAllKeyValueData(XmlFileSystemResource xmlResource) throws XmlDataProviderException {
        logger.entering(xmlResource);
        if (null == xmlResource.getCls()) {
            xmlResource.setCls(KeyValueMap.class);
        }

        Object[][] objectArray = null;
        try {
            JAXBContext context = JAXBContext.newInstance(xmlResource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(xmlResource.getInputStream());
            LinkedHashMap<String, KeyValuePair> keyValueItems = unmarshaller
                    .unmarshal(xmlStreamSource, KeyValueMap.class).getValue().getMap();
            objectArray = DataProviderHelper.convertToObjectArray(keyValueItems);
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new XmlDataProviderException("Error unmarshalling XML file.", excp);
        }

        logger.exiting();
        return objectArray;
    }

    /**
     * Generates a two dimensional array for TestNG DataProvider from the XML data representing a map of name value
     * collection filtered by keys.
     * 
     * A name value item should use the node name 'item' and a specific child structure since the implementation depends
     * on {@link KeyValuePair} class. The structure of an item in collection is shown below where 'key' and 'value' are
     * child nodes contained in a parent node named 'item' :-
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
     * @param xmlResource
     *            A {@link XmlFileSystemResource} object containing the XML file location and representing type.
     * @param keys
     *            The string keys to filter the data.
     * @return A two dimensional object array.
     * @throws XmlDataProviderException
     *             when the XML content in file is invalid, or cannot be unmarshalled to declared type.
     */
    public static Object[][] getDataByKeys(XmlFileSystemResource xmlResource, String[] keys)
            throws XmlDataProviderException {
        logger.entering(xmlResource);
        if (null == xmlResource.getCls()) {
            xmlResource.setCls(KeyValueMap.class);
        }

        Object[][] objectArray = null;
        try {
            JAXBContext context = JAXBContext.newInstance(xmlResource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(xmlResource.getInputStream());
            LinkedHashMap<String, KeyValuePair> keyValueItems = unmarshaller
                    .unmarshal(xmlStreamSource, KeyValueMap.class).getValue().getMap();
            objectArray = DataProviderHelper.getDataByKeys(keyValueItems, keys);
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new XmlDataProviderException("Error unmarshalling XML file.", excp);
        }

        logger.exiting();
        return objectArray;
    }

    /**
     * Generates a list of the declared type after parsing the XML file.
     * 
     * @param xmlResource
     *            A {@link XmlFileSystemResource} object containing the XML file location and representing type.
     * @return A {@link List} of object of declared type {@link XmlFileSystemResource#getCls()}.
     * @throws XmlDataProviderException
     *             when the XML content in file is invalid, or cannot be unmarshalled to declared type.
     */
    private static List<?> loadDataFromXmlFile(XmlFileSystemResource xmlResource) throws XmlDataProviderException {
        logger.entering(xmlResource);
        Preconditions.checkArgument(xmlResource.getCls() != null, "Please provide a valid type.");
        List<?> returned = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class, xmlResource.getCls());
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StreamSource xmlStreamSource = new StreamSource(xmlResource.getInputStream());
            Wrapper<?> wrapper = unmarshaller.unmarshal(xmlStreamSource, Wrapper.class).getValue();
            returned = wrapper.getList();
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new XmlDataProviderException("Error unmarshalling XML file.", excp);
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
     * @throws XmlDataProviderException
     *             when the XML data is invalid, or cannot be unmarshalled to declared type.
     */
    private static List<?> loadDataFromXml(String xml, Class<?> cls) throws XmlDataProviderException {
        logger.entering(new Object[] { xml, cls });
        Preconditions.checkArgument(cls != null, "Please provide a valid type.");
        List<?> returned = null;

        try {
            JAXBContext context = JAXBContext.newInstance(Wrapper.class, cls);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader xmlStringReader = new StringReader(xml);
            StreamSource streamSource = new StreamSource(xmlStringReader);
            Wrapper<?> wrapper = unmarshaller.unmarshal(streamSource, Wrapper.class).getValue();
            returned = wrapper.getList();
        } catch (JAXBException excp) {
            logger.exiting(excp.getMessage());
            throw new XmlDataProviderException("Error unmarshalling XML string.", excp);
        }

        logger.exiting(returned);
        return returned;
    }

    /**
     * Loads the XML data from the {@link XmlFileSystemResource} into a {@link org.dom4j.Document}.
     * 
     * @param xmlResource
     *            The XML data file to load.
     * @return A Document object.
     * @throws XmlDataProviderException
     *             when the XML content in file is invalid, or cannot be unmarshalled to declared type.
     */
    private static Document getDocument(XmlFileSystemResource xmlResource) throws XmlDataProviderException {
        logger.entering(xmlResource);
        DOMDocumentFactory domFactory = new DOMDocumentFactory();
        SAXReader reader = new SAXReader(domFactory);
        Document doc = null;

        try {
            doc = reader.read(xmlResource.getInputStream());
        } catch (DocumentException excp) {
            logger.exiting(excp.getMessage());
            throw new XmlDataProviderException("Error reading XML data.", excp);
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
    private static String getFilteredXml(Document document, String xpathExpression) {
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

    /**
     * Traverses the object graph by following an XPath expression and returns the desired type from object matched at
     * the XPath.
     * 
     * Supports single object retrieval. Also see {@link DataProviderHelper#readListByXpath(Object, Class, String)}.
     * 
     * Note: Need {@code object} and {@code cls} to have getter and setter properties defined to allow object graph
     * traversal.
     * 
     * @param object
     *            An object of any type
     * @param cls
     *            Type of the property being evaluated at the given {@code xpath}.
     * @param xpath
     *            The XPath expression equivalent to the object graph.
     * @return An object of desired type.
     */
    @SuppressWarnings("unchecked")
    public static <T> T readObjectByXpath(Object object, Class<T> cls, String xpath) {
        logger.entering(new Object[] { object, cls, xpath });
        JXPathContext context = JXPathContext.newContext(object);
        T value = (T) context.getValue(xpath);
        logger.exiting(value);
        return value;
    }

    /**
     * Traverses the object graph by following an XPath expression and returns a list of desired type from object
     * matched at the XPath.
     * 
     * Only supports multiple object retrieval as a list. See // *
     * {@link DataProviderHelper#readObjectByXpath(Object, Class, String)} for single object retrieval.
     * 
     * Note: Need {@code object} and {@code cls} to have getter and setter properties defined to allow object graph
     * traversal.
     * 
     * @param object
     *            An object of any type
     * @param cls
     *            Type of the property being evaluated at the given {@code xpath}.
     * @param xpath
     *            The XPath expression equivalent to the object graph.
     * @return An object of desired type.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static <T> List<T> readListByXpath(Object object, Class<T> cls, String xpath) {
        logger.entering(new Object[] { object, cls, xpath });
        JXPathContext context = JXPathContext.newContext(object);
        List<T> values = new ArrayList<T>();
        for (Iterator iter = context.iterate(xpath); iter.hasNext();) {
            T value = (T) iter.next();
            values.add(value);
        }
        logger.exiting(values);
        return values;
    }

}