@DataProvider(name = "xmlDataProvider")
public Object[][] dataXmlDataProvider() throws IOException {
  XmlDataSource resource =
    new FileSystemResource("path/to/trialXmlFile.xml", Address.class);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  return dataProvider.getAllData(new String[] { "k2" });
}