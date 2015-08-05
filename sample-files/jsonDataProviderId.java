@DataProvider(name = "jsonDataProvider")
public Object[][] getJsonDataProvider() throws IOException {
  DataResource resource =
    new InputStreamResource(new FileInputStream("path/to/trialJsonFile.json"),
                            USER.class, "json");
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  Object[][] data = dataProvider.getAllData();
  return data;
}