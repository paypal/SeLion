@DataProvider(name = "xmlDataProvider")
public Iterator<Object[]> getXmlDataProvider() throws IOException, 
  DataProviderException {

    XmlDataSource resource = new XmlInputStreamResource(new BufferedInputStream(
      FileAssistant.loadFile("path/to/trialXmlFile.xml")), Address.class, "xml");
    SeLionDataProvider dataProvider = 
      DataProviderFactory.getDataProvider(resource);
    SimpleIndexInclusionFilter filter = new SimpleIndexInclusionFilter("1,3,5");
    Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
    return data;
}