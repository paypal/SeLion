@DataProvider(name = "xmlDataProvider")
public Object[][] dataXmlDataProvider() throws IOException {
  Map<String, Class<?>> map = new LinkedHashMap<String, Class<?>>();
  map.put("//transactions/transaction/user[1]", User.class);
  map.put("//transactions/transaction/user[2]", User.class);
  XmlDataSource resource =
    new XmlFileSystemResource("path/to/trialXmlFile.xml", map);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  return dataProvider.getAllData();
}