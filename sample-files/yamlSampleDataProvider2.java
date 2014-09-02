@DataProvider(name = "DataProvider")
public Object[][] notSimpleDataProvider() throws Exception {
  String indexes = "1-3,7, 10-12";
  return YamlDataProvider.getDataByIndex(
    new YamlResource("src/test/resources/multipleDataYamlDataFile.yaml"),
    indexes);
}