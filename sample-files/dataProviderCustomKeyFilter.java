@DataProvider(name = "xmlDataProvider")
public Iterator<Object[]> getXmlDataProvider() throws IOException, 
    DataProviderException {

  XmlDataSource resource = 
    new XmlFileSystemResource("path/to/trialXmlFile.xml", Address.class);
  SeLionDataProvider dataProvider = 
    DataProviderFactory.getDataProvider(resource);
  CustomKeyFilter filter = new CustomKeyFilter("street", "1234 Elm st");
  Iterator<Object[]> data = dataProvider.getDataByFilter(filter);
  return data;
}