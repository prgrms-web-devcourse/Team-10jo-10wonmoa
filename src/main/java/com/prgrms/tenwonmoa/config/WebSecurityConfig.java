package com.prgrms.tenwonmoa.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()    // todo: controller 어느 정도 추가되면 antMatcher 재설정
			.antMatchers("/**").permitAll()
			.anyRequest().permitAll()
			.and()
			.csrf()        // disable 하지 않으면 unauthorized
			.disable()
			.httpBasic()    // 토큰을 사용하므로 basic 인증 disable
			.disable()
			.sessionManagement()    // 토큰을 사용하므로 stateless
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
	}
}