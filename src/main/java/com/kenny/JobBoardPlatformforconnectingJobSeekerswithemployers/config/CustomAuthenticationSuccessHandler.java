package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.config;

import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UserService userService;
	private Logger logger = Logger.getLogger(getClass().getName());
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		//System.out.println("\n\nIn customAuthenticationSuccessHandler\n\n");
		logger.info("\n\nIn customAuthenticationSuccessHandler\n\n");
		String userName = authentication.getName();

		List<String> roles = authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
						.collect(Collectors.toList());
		for(String role:roles){
			logger.info(">>>>>>>>>>role = " + role);
		}
		//System.out.println("userName=" + userName);
		logger.info("userName=" + userName);

		User theUser = userService.findByUserName(userName);
		
		// now place in the session
		HttpSession session = request.getSession();
		session.setAttribute("user", theUser);
		session.setAttribute("roles", roles);
		// forward to home page
		
//		response.sendRedirect(request.getContextPath() + "/jobs/showFormForAdd");
//	response.sendRedirect(request.getContextPath() + "/jobs/list");
		//response.sendRedirect(request.getContextPath() + "/base");
		response.sendRedirect(request.getContextPath() + "/Dashboard");
		//response.sendRedirect(request.getContextPath() + "/email/email-attachment");
	}

}
