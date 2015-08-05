@DataProvider(name = "yamlDataProvider")
public Object[][] getYamlDataProvider() throws Exception {
  FileSystemResource resource =
    new FileSystemResource("path/to/trialYamlDataFile.yaml");
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  String indexes = "1-3,7, 10-12";
  return dataProvider.getDataByIndex(indexes);
}