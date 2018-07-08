package org.sid.sec;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sid.entities.AppUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{
	private AuthenticationManager authenticationManager;
	
	public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
		super();
		this.authenticationManager = authenticationManager;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		AppUser appUser=null;
		//ici c'est lorqu'on veux envoyer les donnees au forma normal
		//String username=request.getParameter("username");
		//String password=request.getParameter("password");
		try {
			appUser=new ObjectMapper().readValue(request.getInputStream(), AppUser.class);
		
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		System.out.println("*********************");
		System.out.println("username:"+appUser.getUsername());
		System.out.println("pass:"+appUser.getPassword());
		return authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(appUser.getUsername(), appUser.getPassword()));
	}
	
	@Override
	protected void successfulAuthentication(
			HttpServletRequest request, HttpServletResponse response, 
			FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		//recuperation de l'utilisateur connecte
		User springUser=(User) authResult.getPrincipal();
		//generer un token pour ce utilisateur
		String jwt=Jwts.builder()
				.setSubject(springUser.getUsername())
				.setExpiration(new Date(System.currentTimeMillis()+SecurityConstants.EXPIRATION_TIME))
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.SECRET)
				.claim("roles", springUser.getAuthorities())
				.compact();
		response.addHeader(SecurityConstants.HEADER_STRING, SecurityConstants.TOKEN_PREFIX+jwt);
		super.successfulAuthentication(request, response, chain, authResult);
	}

}
