@DataProvider(name = "yamlDataProvider")
public Object[][] simpleDataProvider() throws Exception {
  return YamlDataProvider.getAllData(
    new YamlResource("src/test/resources/trialYamlDataFile.yaml"));
}