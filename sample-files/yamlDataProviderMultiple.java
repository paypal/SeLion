@DataProvider(name = "yamlDataProvider")
public Object[][] getYamlDataProvider() throws Exception {
  List<DataResource> yamlResources = new ArrayList<>();
  yamlResources.add(new FileSystemResource(pathName,
		                                   userDocuments, USER.class));
  yamlResources.add(new FileSystemResource(pathName,
		                                   cityDocuments, CITY.class));
  return DataProviderHelper.getAllDataMultipleArgsFromYAML(yamlResources);
}