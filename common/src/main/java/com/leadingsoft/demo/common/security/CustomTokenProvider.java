package com.leadingsoft.demo.common.security;

import java.security.Provider;
import java.security.Security;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadingsoft.bizfuse.common.web.utils.json.JsonUtils;
import com.leadingsoft.bizfuse.common.webauth.config.jwt.TokenProvider;
import com.leadingsoft.bizfuse.common.webauth.util.SecurityUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.CompressionCodec;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.compression.DeflateCompressionCodec;

/**
 * JWT Token 和 Security AuthenticationToken 提供相互转换的实现
 *
 * @author liuyg
 */
public class CustomTokenProvider implements TokenProvider {

    private final Logger log = LoggerFactory.getLogger(CustomTokenProvider.class);

    private static final String USERDETAILS_KEY = "details";
    @Value("${security.authentication.jwt.secret}")
    private String secretKey;
    @Value("${security.authentication.jwt.tokenValidityInSeconds: 1800}")
    private long tokenValidityInSeconds;
    @Value("${security.authentication.jwt.tokenValidityInSeconds: 2592000}")
    private long tokenValidityInSecondsForRememberMe;
    @Autowired
    private ObjectMapper objectMapper;

    private final CompressionCodec compression = new DeflateCompressionCodec();

    public void addSecurityProvider(final Provider provider) {
        Security.insertProviderAt(provider, 1);
    }

    @Override
    public String getLoginUserToken() {
        if (!SecurityUtils.isAuthenticated()) {
            return null;
        }
        final CustomAuthenticationToken auth =
                (CustomAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        String token = auth.getToken();
        if (token == null) {
            token = this.createToken(auth, false);
            auth.setToken(token, this.getExpiration(false).getTime());
        }
        return token;
    }

    @Override
    public String createToken(final Authentication authentication, final Boolean rememberMe) {
        final Date expiration = this.getExpiration(rememberMe);
        return this.createToken(authentication, expiration);
    }

    protected String createToken(final Authentication authentication, final Date expiration) {
        final Object loginUserBean = authentication.getDetails();
        String strToken = null;
        try {
            if (loginUserBean != null) {
                strToken = this.objectMapper.writeValueAsString(loginUserBean);
            }
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("token details is not Json Object.", e);
        }
        return Jwts.builder()
                .compressWith(this.compression)
                .claim(CustomTokenProvider.USERDETAILS_KEY, strToken)
                .signWith(SignatureAlgorithm.HS512, this.secretKey)
                .setExpiration(expiration)
                .compact();
    }

    @Override
    public Authentication getAuthentication(final String token) {
        final Claims claims = Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        final String json = claims.get(CustomTokenProvider.USERDETAILS_KEY, String.class);
        final LoginUserBean currentUser = JsonUtils.jsonToPojo(json, LoginUserBean.class);
        final CustomAuthenticationToken authentication = new CustomAuthenticationToken();
        authentication.setAuthenticated(true);
        authentication.setDetails(currentUser);
        return authentication;
    }

    @Override
    public boolean validateToken(final String authToken) {
        try {
            Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(authToken);
            return true;
        } catch (final SignatureException e) {
            this.log.info("Invalid JWT signature: " + e.getMessage());
            return false;
        }
    }

    private Date getExpiration(final Boolean rememberMe) {
        final long now = (new Date()).getTime();

        if (rememberMe) {
            return new Date(now + (this.tokenValidityInSecondsForRememberMe * 1000));
        } else {
            return new Date(now + (this.tokenValidityInSeconds * 1000));
        }
    }
}
