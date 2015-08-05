@DataProvider(name = "xmlDataProvider")
public Object[][] getXmlDataProvider() throws IOException {
  XmlDataSource resource =
    new XmlFileSystemResource("path/to/trialXmlFile.xml", KeyValueMap.class);
  XmlDataProvider dataProvider =
    (XmlDataProvider) DataProviderFactory.getDataProvider(resource);
  Object[][] data = dataProvider.getAllKeyValueData();
  return data;
}