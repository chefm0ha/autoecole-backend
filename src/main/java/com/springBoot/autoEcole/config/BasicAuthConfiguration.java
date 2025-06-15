package com.springBoot.autoEcole.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class BasicAuthConfiguration extends WebSecurityConfigurerAdapter {
     
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
     
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
         
        return authProvider;
    }
 
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }
 
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .antMatchers("/user/getUser").authenticated()
            .anyRequest().permitAll()
            .and()
            .formLogin()
                .usernameParameter("email")
                .defaultSuccessUrl("/users")
                .permitAll()
            .and()
            .logout().logoutSuccessUrl("/").permitAll()
            .and()
            .csrf().disable();
    }

	
	/*@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
      http.csrf().disable()
      .authorizeRequests()
      .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
      .antMatchers("/user/getUser").permitAll()
      .anyRequest()
      .authenticated()
      .and()
      .httpBasic();

	}	
	
	@Bean
	public BCryptPasswordEncoder  passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}*/

//    @Override
//    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//          .inMemoryAuthentication()
//          .withUser("user")
//          .password("{noop}password")
//          .roles("USER");
//    }
//
//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        http.csrf().disable()
//          .authorizeRequests()
//          .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
//          .antMatchers("/user/login").permitAll()
//          .anyRequest()
//          .authenticated()
//          .and()
//          .httpBasic();
//    }
}