package com.Logisight.util;
import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.DeviceType;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;

public class UserAgentUtil {

    public static UserAgent parseUserAgent(HttpServletRequest request) {
        String uaString = request.getHeader("User-Agent");
        return UserAgent.parseUserAgentString(uaString);
    }

    public static String getBrowser(HttpServletRequest request) {
        return parseUserAgent(request).getBrowser().getName();
    }

    public static String getBrowserVersion(HttpServletRequest request) {
        return parseUserAgent(request).getBrowserVersion() != null
            ? parseUserAgent(request).getBrowserVersion().getVersion()
            : "unknown";
    }

    public static String getOperatingSystem(HttpServletRequest request) {
        return parseUserAgent(request).getOperatingSystem().getName();
    }

    public static String getDeviceType(HttpServletRequest request) {
        DeviceType type = parseUserAgent(request).getOperatingSystem().getDeviceType();
        return type != null ? type.getName() : "unknown";
    }

    public static boolean isMobile(HttpServletRequest request) {
        return parseUserAgent(request).getOperatingSystem().getDeviceType() == DeviceType.MOBILE;
    }

    public static boolean isTablet(HttpServletRequest request) {
        return parseUserAgent(request).getOperatingSystem().getDeviceType() == DeviceType.TABLET;
    }

    public static boolean isDesktop(HttpServletRequest request) {
        return parseUserAgent(request).getOperatingSystem().getDeviceType() == DeviceType.COMPUTER;
    }
}
