package com.Logisight.util;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.Logisight.security.JwtProperties;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.Data;

@Component
@Data
public class JwtUtil {

	private final SecretKey secretKey;
	private final long expirationMillis;
	
	  public JwtUtil(@Qualifier("jwtProperties") JwtProperties props) {
	        this.secretKey = Keys.hmacShaKeyFor(props.getSecret().getBytes());
	        this.expirationMillis = props.getExpiration();
	    }
	  
	  public String generateToken(String username, String role, String sessionId) {
		    Date now = new Date();
		    Date expiryDate = new Date(now.getTime() + expirationMillis);

		    return Jwts.builder()
		            .setSubject(username)
		            .claim("role", role)
		            .claim("sessionId", sessionId)
		            .setIssuedAt(now)
		            .setExpiration(expiryDate)
		            .signWith(secretKey, SignatureAlgorithm.HS512)
		            .compact();
		}
	  public String getSessionIdFromToken(String token) {
		    return parseToken(token).getBody().get("sessionId", String.class);
		}

		public String getRoleFromToken(String token) {
		    return parseToken(token).getBody().get("role", String.class);
		}
	  
	  public String getUsernameFromToken(String token) {
	        return parseToken(token).getBody().getSubject();
	    }

	    public boolean validateToken(String token) {
	        try {
	            parseToken(token);
	            return true;
	        } catch (JwtException | IllegalArgumentException e) {
	            return false;
	        }
	    }

	    private Jws<Claims> parseToken(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(secretKey)
	                .build()
	                .parseClaimsJws(token);
	    }
}
