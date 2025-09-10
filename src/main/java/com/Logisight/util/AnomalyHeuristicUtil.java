package com.Logisight.util;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

public class AnomalyHeuristicUtil {

    private static final long MIN_CLICK_INTERVAL_MS = 300; // çok hızlı tıklarsa bot olabilir
    private static final int MAX_REQUESTS_PER_SECOND = 10;
    private static final Pattern SUSPICIOUS_UA = Pattern.compile("(?i)(bot|crawl|spider|scanner|wget|curl)");

    /**
     * 1. Aşırı hızlı tıklama kontrolü (ms bazında)
     */
    public static boolean isClickTooFast(ZonedDateTime previous, ZonedDateTime current) {
        long interval = Duration.between(previous, current).toMillis();
        return interval < MIN_CLICK_INTERVAL_MS;
    }

    /**
     * 2. Request frekansı: saniyede 10'dan fazla istek
     */
    public static boolean isTooManyRequestsPerSecond(int requestsInWindow, Duration windowDuration) {
        double rate = (double) requestsInWindow / windowDuration.toSeconds();
        return rate > MAX_REQUESTS_PER_SECOND;
    }

    /**
     * 3. User-Agent bot kontrolü
     */
    public static boolean isSuspiciousUserAgent(String userAgent) {
        if (userAgent == null) return true;
        return SUSPICIOUS_UA.matcher(userAgent).find();
    }

    /**
     * 4. Aynı IP'den çok fazla hesap girişimi (örnek eşik)
     */
    public static boolean isMassLoginFromIp(int loginAttemptsFromIp, int threshold) {
        return loginAttemptsFromIp > threshold;
    }

    /**
     * 5. Gece 3-5 arası insan aktivitesi düşükse bot olabilir (isteğe bağlı)
     */
    public static boolean isOddHourAccess(ZonedDateTime dateTime) {
        int hour = dateTime.getHour();
        return hour >= 3 && hour <= 5;
    }
}
