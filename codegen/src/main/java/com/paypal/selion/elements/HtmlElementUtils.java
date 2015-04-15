package com.paypal.selion.elements;

public class HtmlElementUtils {
    
    /**
     * Extracts the package from a qualified class.
     * 
     * @param element string of the qualified class
     * @return package
     */
    public static String getPackage(String element) {
        return element.substring(0, element.lastIndexOf('.'));
    }
    
    /**
     * Extracts the class name from a qualified class.
     * 
     * @param element string of the qualified class
     * @return class name
     */
    public static String getClass(String element) {
        return element.substring(element.lastIndexOf('.') + 1);
    }
}
