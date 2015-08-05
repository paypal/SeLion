@DataProvider(name = "yamlDataProvider")
public Object[][] getYamlDataProvider() throws IOException {
  FileSystemResource resource =
    new FileSystemResource("path/to/trialYamlDataFile.yaml");
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  return dataProvider.getAllData();
}