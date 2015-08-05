public enum Country {

  UNITED_STATES("us"),
  UNITED_KINGDOM("gb"),
  INDIA("in");

  private Country (String code) {
    this.code = code;
  }

  private String code;

  public static Country getCountry(String code){
    for(Country each : Country.values()){
      if (each.code.equalsIgnoreCase(code)){
        return each;
      }
    }
    return null;
  }
}