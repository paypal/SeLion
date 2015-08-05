@DataProvider(name = "excelDataProvider")
public Object[][] getExcelDataProvider() throws Exception {
  DataResource resource = 
    new FileSystemResource("src/test/resources/testdata/MyDataFile.xls", 
                           Custom.class);
  SeLionDataProvider dataProvider =
    DataProviderFactory.getDataProvider(resource);
  ExcelDataProvider excelDataProvider =
    ExcelDataProvider.class.cast(dataProvider);
  Method method = Country.class.getMethod("getCountry", String.class);
  DefaultCustomType type = new DefaultCustomType(Country.class, method);
  dataProvider.addCustomTypes(type);
  return dataProvider.getAllData();
}