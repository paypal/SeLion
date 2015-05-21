package com.paypal.selion.grid.matchers;

import java.util.Map;

import org.openqa.grid.internal.utils.CapabilityMatcher;
import org.uiautomation.ios.IOSCapabilities;

final class MinimalIOSCapabilityMatcher implements CapabilityMatcher {

    private static final String BUNDLE_NAME = IOSCapabilities.BUNDLE_NAME;

    @Override
    public boolean matches(Map<String, Object> currentCapability, Map<String, Object> requestedCapability) {
        // Simply checks for CFBundleIdentifier and CFBundleName
        return (isValid(currentCapability) && isValid(requestedCapability));
    }

    /*
     * Checks the validity of a capability by checking for not-null reference and the availability of CFBundleIdentifier
     * and CFBundleName keys.
     */
    private boolean isValid(Map<String, Object> capability) {
        boolean validCapability = false;
        validCapability = capability != null && capability.containsKey(BUNDLE_NAME);
        return validCapability;
    }
}