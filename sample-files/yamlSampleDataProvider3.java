@DataProvider(name = "MultipleSources")
public static Object[][] dataProviderGetMultipleArguments() throws Exception {
  List yamlResources = new ArrayList();
  yamlResources.add(new YamlResource(pathName, userDocuments, USER.class));
  yamlResources.add(new YamlResource(pathName, cityDocuments, CITY.class));
  Object[][] data = new YamlDataProvider().getAllDataMultipleArgs(yamlResources);

  return data;
}