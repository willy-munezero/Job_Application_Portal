package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service;



import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao.JobRepository;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.Job;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    private JobRepository jobRepository;
    private UserService userService;

    private EntityManager entityManager;

    @Autowired
    public JobServiceImpl(JobRepository theJobRepository, UserService theuserService, EntityManager theentityManager) {
        jobRepository = theJobRepository;
        this.userService = theuserService;
        this.entityManager = theentityManager;
    }

    @Override
    public List<Job> findAll() {
        // create query
        TypedQuery<Job> theQuery = entityManager.createQuery("SELECT j FROM Job j ORDER BY j.jobTitle ASC", Job.class);

        // return query results
        return theQuery.getResultList();
    }


    // Pageable findAll

    @Override
    public Page<Job> findAll(int pageNumber, String keyword) {
        Sort sort = Sort.by("jobTitle").ascending();

        Pageable pageable = PageRequest.of(pageNumber - 1, 2, sort);
        if(keyword !=null){
            return jobRepository.findAll(keyword, pageable);
        }
        return jobRepository.findAll(pageable);
    }


    @Override
    public Job findById(Long theId) {
        Optional<Job> result = jobRepository.findById(theId);

        Job job = null;

        if (result.isPresent()) {
            job = result.get();
        }
        else {
            // we didn't find the project
            throw new RuntimeException("Did not find job id - " + theId);
        }

        return job;
    }


    @Override
    @Transactional
    public void save(Job job, Long UserId) {
        User existingUser = userService.findById(UserId);
        existingUser.add(job);
        jobRepository.save(job);
    }

    @Override
    public void deleteById(Long theId) {
        jobRepository.deleteById(theId);
    }
}
