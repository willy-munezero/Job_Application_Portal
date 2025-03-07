package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service;



import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao.RoleDao;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao.UserDao;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.Role;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

	// need to inject user dao
	@Autowired
	private UserDao userDao;

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	private Logger logger = Logger.getLogger(getClass().getName());
	@Override
	@Transactional
	public User findByUserName(String userName) {
		// check the database if the user already exists
		return userDao.findByUserName(userName);
	}

	@Override
	@Transactional
	public User findById(Long id) {
		// check the database if the user already exists
		return userDao.findById(id);
	}

	@Override
	@Transactional
	public User findByEmail(String email) {
		// check the database if the user already exists
		return userDao.findByEmail(email);
	}


	@Override
	@Transactional
	public User save(User us, List<GrantedAuthority> authorities) {
		//User user = new User();
		// assign user details to the user object

		//user.setPassword(passwordEncoder.encode(om.getPassword()));
		us.setPassword(passwordEncoder.encode(us.getPassword()));

		User user = new User(us.getFirstName(), us.getLastName(), us.getEmail(),
		us.getPhoneNumber(), us.getPassword(), us.getUsername());
		List<Role> roles = new ArrayList<>();
		for(GrantedAuthority auth:authorities){
			Role role = roleDao.findRoleByName(auth.toString());
			roles.add(role);
			//user.setRoles(Arrays.asList(roleDao.findRoleByName(auth.toString())));
		}
		us.setRoles(roles);
		// save user in the database
		userDao.save(us);
		return user;
	}

	@Override
	@Transactional
	public User findByEmailAndPhoneNumber(String email, String phoneNumber) {
		return userDao.findByEmailAndPhoneNumber(email,phoneNumber);
	}

	@Override
	@Transactional
	public User save(User user) {
		return userDao.save(user);
	}


	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userDao.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				mapRolesToAuthorities(user.getRoles()));
	}

	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
		return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
	}
}
