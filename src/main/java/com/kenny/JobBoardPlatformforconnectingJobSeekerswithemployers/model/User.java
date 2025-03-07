package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "first_name")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String firstName;
    @Column(name = "last_name")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String lastName;
    @Column(name = "email")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String email;
    @Column(name = "username")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String username;
    @Column(name = "password")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String password;
    @Column(name = "phone_number")
    @NotNull(message="is Required")
    @Size(min=1, message="is required")
    private String phoneNumber;
    //@NotNull(message="is Required")
    @Transient
    private String formRole;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Collection<Role> roles;


    @OneToMany(fetch=FetchType.LAZY, mappedBy="user",
            cascade= {CascadeType.DETACH, CascadeType.MERGE, CascadeType.PERSIST,
                    CascadeType.REFRESH})
    private List<Job> jobs;

    public User(){

    }

    public void add(Job tempJob)
    {
        if(jobs == null)
        {
            jobs = new ArrayList<>();
        }

        jobs.add(tempJob);
        tempJob.setUser(this);
    }


    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String firstName, String lastName,
                String email, String username, String password, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }



    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFormRole() {
        return formRole;
    }

    public void setFormRole(String formRole) {
        this.formRole = formRole;
    }

    public Collection<Role> getRoles() {
        return roles;
    }

    public void setRoles(Collection<Role> roles) {
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public void addRole(Role role) {
        if (roles == null) {
            roles = new ArrayList<>();
        }
        roles.add(role);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", formRole='" + formRole + '\'' +
//                ", roles=" + roles +
                ", jobs=" + jobs +
                '}';
    }
}
