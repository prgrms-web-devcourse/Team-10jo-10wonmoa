package com.prgrms.tenwonmoa.domain.user.security.jwt.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.prgrms.tenwonmoa.domain.user.security.jwt.Jwt;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class TokenProvider {

	private final Jwt jwt;

	public String generateAccessToken(Long userId, String email, Date now) {
		JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
		builder.withIssuer(jwt.getIssuer());
		builder.withIssuedAt(now);
		if (jwt.getExpirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwt.getExpirySeconds()));
		}
		builder.withClaim("userId", userId)
			.withClaim("email", email);
		return builder.sign(jwt.getAlgorithm());
	}

	public String generateRefreshToken(String email, Date now) {
		JWTCreator.Builder builder = com.auth0.jwt.JWT.create();
		builder.withIssuer(jwt.getIssuer());
		builder.withIssuedAt(now);
		if (jwt.getExpirySeconds() > 0) {
			builder.withExpiresAt(new Date(now.getTime() + jwt.getRefreshExpirySeconds()));
		}
		builder.withClaim("email", email);
		return builder.sign(jwt.getAlgorithm());
	}

	public Long validateAndGetUserId(String token) {
		Jwt.Claims claims = verifyAndGetClaims(token);
		return claims.getUserId();
	}

	public String validateAndGetEmail(String token) {
		Jwt.Claims claims = verifyAndGetClaims(token);
		return claims.getEmail();
	}

	public String validateAndGetExpiredIn(String token) {
		Jwt.Claims claims = verifyAndGetClaims(token);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(claims.getExp());
	}

	private Jwt.Claims verifyAndGetClaims(String token) {
		DecodedJWT decodedJwt = jwt.getJwtVerifier().verify(token);
		return new Jwt.Claims(decodedJwt);
	}

}
