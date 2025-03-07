package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.config;



import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DemoSecurityConfig extends WebSecurityConfigurerAdapter {

	// add a reference to our user service
    @Autowired
    private UserService userService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

   @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.authorizeRequests()
//				.antMatchers("/").hasRole("JOB_SEEKER")
				.antMatchers("/jobs/showFormForAdd").permitAll()
				.antMatchers("/jobs/*").authenticated()
				.antMatchers("/email/*").authenticated()
				.antMatchers("/css/**").permitAll()
				.antMatchers("/jobs/showFormForAdd").hasRole("EMPLOYEER")
				.antMatchers("/jobs/list").hasRole("JOB_SEEKER")
//				.antMatchers("/Manager/show").hasRole("OPERATIONS_MANAGER")
//
				.antMatchers("/Manager/show").permitAll()
//				.antMatchers("/showMyLoginPage").permitAll()
				.antMatchers("/templates/**", "/static/**").permitAll()
				//.antMatchers("/leaders/**").hasRole("MANAGER")
				//.antMatchers("/systems/**").hasRole("ADMIN")
				.anyRequest().authenticated()
				.and()
				.formLogin()
				.loginPage("/showMyLoginPage")
				.loginProcessingUrl("/authenticateTheUser")
				.successHandler(customAuthenticationSuccessHandler)
				.permitAll()
				.and()
				.logout().permitAll()
				.and()
				.exceptionHandling().accessDeniedPage("/access-denied");

	}

	//beans
	//bcrypt bean definition
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//authenticationProvider bean definition
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(userService); //set the custom user details service
		auth.setPasswordEncoder(passwordEncoder()); //set the password encoder - bcrypt
		return auth;
	}
	  
}






