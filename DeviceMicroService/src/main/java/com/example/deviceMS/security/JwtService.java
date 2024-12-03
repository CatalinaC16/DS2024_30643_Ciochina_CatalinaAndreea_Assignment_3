package com.example.deviceMS.security;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private static final String SECRET_KEY = "eNNhQ4S3KK6Whd686pnwgJDzJ6mFGn8phfkd9mwZ8hGquceTe2CnaMeBkmgFdhEt";

    public boolean isTokenValid(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
