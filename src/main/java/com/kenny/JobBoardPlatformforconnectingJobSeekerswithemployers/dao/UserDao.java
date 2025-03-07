package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao;


import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;

import java.util.List;

public interface UserDao {

    public User findByUserName(String userName);

    public User save(User user);

    public User findByEmail(String email);

    public User findById(Long id);

    public User findByEmailAndPhoneNumber(String email, String phoneNumber);
    public List<User> findUsers(int page, int size);

    public List<User> findUsers(int page, int size, String searchTerm);

    public long countUsers();



}
