@DataProvider(name = "DataProvider")
public Object[][] specificKeyDataProvider() throws Exception {
  String[] keyArray = getListOfRelevantKeys();
  return YamlDataProvider.getDataByKeys(
    new YamlResource("src/test/resources/multipleDataYamlDataFile.yaml"),
    keyArray);
}