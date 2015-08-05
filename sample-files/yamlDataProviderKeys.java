@DataProvider(name = "yamlDataProvider")
public Object[][] getYamlDataProvider() throws Exception {
  FileSystemResource resource =
    new FileSystemResource("path/to/trialYamlDataFile.yaml");
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  String[] keyArray = new String[] {"test2"};
  return dataProvider.getDataByKeys(keyArray);
}