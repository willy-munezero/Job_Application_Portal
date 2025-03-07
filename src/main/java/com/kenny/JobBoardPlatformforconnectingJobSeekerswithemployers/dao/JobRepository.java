package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao;



import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobRepository extends PagingAndSortingRepository<Job, Long> {
    //searching using a keyword
    @Query("SELECT j FROM Job j WHERE "
            + "CONCAT(j.jobTitle, j.location, j.experience, j.category, j.level, j.jobDescription," +
            " j.deadline, j.skill, j.companyEmail, j.phoneNumber)" + "LIKE %?1%")
    public Page<Job> findAll(String keyword, Pageable pageable);
}
