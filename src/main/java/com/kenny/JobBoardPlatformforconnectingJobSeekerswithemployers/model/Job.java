package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model;


import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "job")
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    @NotNull(message = "Job title is required")
    private String jobTitle;

    @NotNull(message = "Location is required")
    private String location;

    @NotNull(message = "Experience is required")
    private String experience;

    @NotNull(message = "Category is required")
    private String category;

    @NotNull(message = "Level is required")
    private String level;

    @NotNull(message = "Job description is required")
    private String jobDescription;

    private String applicationGuideline;
    @NotNull(message = "Deadline is required")
    @DateTimeFormat(pattern ="yyyy-MM-dd")
    private String deadline;
    @NotNull(message = "Skills are required")
    private String skill;
    @NotNull(message = "Company email is required")
    @Pattern(regexp = "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}\\b", message = "Invalid email format")
    private String companyEmail;
    @NotNull(message = "Phone number is required")
    @Size(min = 10, max = 15, message = "Phone number must be between 10 and 15 characters")
    private String phoneNumber;

//    @ManyToMany(cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
//            CascadeType.REFRESH})
//    @JoinTable(name="job_users",joinColumns=@JoinColumn(name="job_id"),
//            inverseJoinColumns=@JoinColumn(name="user_id"))
//    private List<User> users=new ArrayList<>();
//    @ManyToMany(fetch = FetchType.LAZY,
//        cascade ={CascadeType.DETACH,CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
//    @JoinTable(
//        name="job_users",
//        joinColumns = @JoinColumn(name="job_id"),
//        inverseJoinColumns = @JoinColumn(name="user_id")
//     )
//    private List<User> users;
@ManyToOne(cascade = {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
@JoinColumn(name = "employer_id", referencedColumnName = "id")
private User user;


    public Job(){
//        this.users = new ArrayList<>();
    }

    public Job(String jobTitle, String location, String skill) {
        this.jobTitle = jobTitle;
        this.location = location;
        this.skill = skill;
    }

    public Job(String jobTitle, String location, String experience, String category, String level, String jobDescription,
               String applicationGuideline, String deadline, String skill, String companyEmail, String phoneNumber) {
        this.jobTitle = jobTitle;
        this.location = location;
        this.experience = experience;
        this.category = category;
        this.level = level;
        this.jobDescription = jobDescription;
        this.applicationGuideline = applicationGuideline;
        this.deadline = deadline;
        this.skill = skill;
        this.companyEmail = companyEmail;
        this.phoneNumber = phoneNumber;
    }

    public Job(String jobTitle, String location, String experience, String category, String level,
               String jobDescription, String deadline, String skill, String companyEmail, String phoneNumber) {
        this.jobTitle = jobTitle;
        this.location = location;
        this.experience = experience;
        this.category = category;
        this.level = level;
        this.jobDescription = jobDescription;
        this.deadline = deadline;
        this.skill = skill;
        this.companyEmail = companyEmail;
        this.phoneNumber = phoneNumber;
    }



    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getApplicationGuideline() {
        return applicationGuideline;
    }

    public void setApplicationGuideline(String applicationGuideline) {
        this.applicationGuideline = applicationGuideline;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Job{" +
                "jobTitle='" + jobTitle + '\'' +
                ", location='" + location + '\'' +
                ", experience='" + experience + '\'' +
                ", category='" + category + '\'' +
                ", level='" + level + '\'' +
                ", jobDescription='" + jobDescription + '\'' +
                ", deadline='" + deadline + '\'' +
                ", skill='" + skill + '\'' +
                ", companyEmail='" + companyEmail + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}

