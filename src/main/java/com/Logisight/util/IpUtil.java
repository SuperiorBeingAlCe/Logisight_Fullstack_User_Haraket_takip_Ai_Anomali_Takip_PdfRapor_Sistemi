package com.Logisight.util;

import jakarta.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;


public class IpUtil {
	 private static final Pattern IPV4_PRIVATE = Pattern.compile(
		        "^(10\\.|192\\.168\\.|172\\.(1[6-9]|2\\d|3[0-1])\\.).*"
		    );

		    private static final Pattern IPV6_LOCAL = Pattern.compile("^::1$");

		    // Gerçek IP'yi alır, proxy varsa X-Forwarded-For üzerinden
		    public static String getClientIp(HttpServletRequest request) {
		        String ip = request.getHeader("X-Forwarded-For");
		        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
		            return ip.split(",")[0].trim(); // İlk IP, client IP olur
		        }
		        ip = request.getHeader("Proxy-Client-IP");
		        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
		            ip = request.getRemoteAddr();
		        }
		        return ip;
		    }

		    // IP adresini anonimleştir (son okteti sıfırla)
		    public static String anonymizeIp(String ip) {
		        if (ip == null) return null;
		        if (ip.contains(".")) {
		            String[] parts = ip.split("\\.");
		            if (parts.length == 4) {
		                parts[3] = "0";
		                return String.join(".", parts);
		            }
		        } else if (ip.contains(":")) {
		            int lastColon = ip.lastIndexOf(':');
		            return ip.substring(0, lastColon) + ":0000";
		        }
		        return ip;
		    }

		    // Yerel bir IP mi?
		    public static boolean isLocalIp(String ip) {
		        if (ip == null) return false;
		        return IPV4_PRIVATE.matcher(ip).matches() || IPV6_LOCAL.matcher(ip).matches() || ip.equals("127.0.0.1");
		    }

		    // Dns çözümlemesi
		    public static boolean isHostnameLocal(String hostname) {
		        try {
		            InetAddress addr = InetAddress.getByName(hostname);
		            return isLocalIp(addr.getHostAddress());
		        } catch (UnknownHostException e) {
		            return false;
		        }
		    }
}
