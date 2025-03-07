package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao;


import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.Role;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;

public interface RoleDao {

	public Role findRoleByName(String theRoleName);

	public Role save(Role role);
	
}
