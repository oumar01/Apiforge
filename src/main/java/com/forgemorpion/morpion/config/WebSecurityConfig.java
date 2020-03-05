package com.forgemorpion.morpion.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.forgemorpion.morpion.config.jwt.JwtAuthenticationEntryPoint;
import com.forgemorpion.morpion.config.jwt.JwtRequestFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
   
   @Autowired
   private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
   
   @Autowired
   private UserDetailsService jwtUserDetailsService;
   
   @Autowired
   private JwtRequestFilter jwtRequestFilter;

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
      // configure AuthenticationManager so that it knows from where to load
      // user for matching credentials
      // Use BCryptPasswordEncoder
      auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder());
   }

   @Override
   protected void configure(HttpSecurity httpSecurity) throws Exception {
      // We don't need CSRF for this example

      httpSecurity.csrf().disable()
      // dont authenticate this particular request
            .authorizeRequests()
            .antMatchers(HttpMethod.POST,  "/users/register").permitAll()
            .antMatchers(HttpMethod.POST, "/users/authenticate").permitAll()
            // all other requests need to be authenticated
            .anyRequest().authenticated().and()
            // make sure we use stateless session; session won't be used to
            // store user's state.
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
      // Add a filter to validate the tokens with every request
      httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
   }

   @Bean
   @Override
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }
   
   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   /**
    * Cors configuration for angular calling
    * @return a simple cors filter for Origins and others
    */
   @Bean
   public FilterRegistrationBean<CorsFilter> simpleCorsFilter() {
      UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
      CorsConfiguration config = new CorsConfiguration();
      config.setAllowCredentials(true);
      config.setAllowedOrigins(Arrays.asList("*")); // Collections.singletonList("http://localhost:4200") pour ajouter les domaines acceptes
      config.setAllowedMethods(Collections.singletonList("*"));
      config.setAllowedHeaders(Collections.singletonList("*"));
      source.registerCorsConfiguration("/**", config);
      FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
      bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
      return bean;
   }
}