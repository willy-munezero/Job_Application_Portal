package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.controller;


import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.Job;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service.JobService;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;
@Controller
@RequestMapping("/jobs")
public class JobController {

    private Logger logger = Logger.getLogger(getClass().getName());

    private UserService userService;

    private JobService jobService;


    private JavaMailSender javaMailSender;





    @Autowired
    public JobController(UserService userService, JobService jobService, JavaMailSender javaMailSender) {
        this.userService = userService;
        this.jobService = jobService;
        this.javaMailSender = javaMailSender;
    }

    @InitBinder
    public void initBinder(WebDataBinder dataBinder)
    {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }

    private static String UPLOAD_DIR = "./uploads/";

    @PostMapping("/save")
    public String saveJob(@RequestParam("guide") MultipartFile guide,
                          @Valid @ModelAttribute("job") Job theJob, User usr,
                          BindingResult theBindingResult, @RequestParam("AuthId") int AuthId,
                          HttpSession session, HttpServletRequest request) {

        // Check if AuthId is not null before using it
//        if (AuthId  null) {
//            // Handle the case where AuthId is null
//            return "error"; // Replace with the appropriate error page URL
//        }

        logger.warning("=========Init binder================");
        if (theBindingResult.hasErrors()) {
            return "job";
        }

        logger.warning("==============Author==============");
        Long userId = (long) AuthId;
        User existingUser = userService.findById(userId);
        logger.warning("\n " + existingUser.toString() + "\n");

        // now Add the userId to the session of future reference in the session
        session = request.getSession();
        if (session.getAttribute("AuthId") == null) {
            session.setAttribute("AuthId", userId);
            logger.warning("================AuthId set as session attribute============");
        }
        logger.warning("========Job========");
        logger.warning(theJob.toString());

        Job dbJob = new Job(theJob.getJobTitle(), theJob.getLocation(), theJob.getExperience(), theJob.getCategory(), theJob.getLevel(), theJob.getJobDescription(),
                theJob.getDeadline(), theJob.getSkill(), theJob.getCompanyEmail(), theJob.getPhoneNumber());

        // Save the file only if a file is provided
        if (!guide.isEmpty()) {
            try {
                byte[] bytes = guide.getBytes();
                String originalFileName = guide.getOriginalFilename();
                Path path = Paths.get(UPLOAD_DIR + originalFileName);

                if (Files.exists(path)) {
                    // File with the same name already exists, do not save
                    logger.info("File with name " + originalFileName + " already exists, skipping save.");
                } else {
                    // Save the file
                    Files.write(path, bytes);
                    dbJob.setApplicationGuideline(originalFileName);
                    logger.info(">>>>>>>>>>>>>>>>>>>>>>Path for file " + path);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String userN = usr.getUsername();
        logger.info("Processing username for: " + userN);
        if (theBindingResult.hasErrors()) {
            System.out.println("Username not found");
        }

        jobService.save(dbJob, userId);

        return "success-job";
    }


    @GetMapping("/showFormForAdd")
    public String showFormForAdd(Model theModel, HttpSession session) {


        //create model atrribute to bind form data
        Job theJob = new Job();
        theModel.addAttribute("job", theJob);
        // get the userRoles list from session and add it to the model
        List<String> userRoles = (List<String>) session.getAttribute("roles");
        User theUser = (User) session.getAttribute("user");
        if((userRoles==null) || theUser == null){
            //response.sendRedirect(request.getContextPath() + "/");
            logger.info("the credentials are null");
            return "redirect:/";
        }

        logger.info(">>>>>>>>>>Current User = " + theUser.getFirstName());
        for(String role:userRoles){
            logger.info(">>>>>>>>>>role = " + role);
        }
        theModel.addAttribute("AuthId", theUser.getId());
        theModel.addAttribute("theUser", theUser);
        return "job";

    }

    @GetMapping("/list")
    public String listEmployees(Model theModel) {

        String keyword = null;

        return listByPage(theModel, 1, keyword);
    }

    @GetMapping("/page/{pageNumber}")
    public String listByPage(Model theModel, @PathVariable("pageNumber") int currentPage,
                             @Param("keyword") String keyword) {


        //pagination
        Page<Job> page = jobService.findAll(currentPage, keyword);
        long totalItems = page.getTotalElements();
        int totalPages = page.getTotalPages();
        // get jobs from DB
        List<Job> theJobs = page.getContent();
//        //get user
//        List<Integer> users = userService.findAllIds();
        //add to the spring model
//        theModel.addAttribute("users", users);
        theModel.addAttribute("currentPage", currentPage);
        theModel.addAttribute("totalItems", totalItems);
        theModel.addAttribute("totalPages", totalPages);
        theModel.addAttribute("theJobs", theJobs);
        theModel.addAttribute("keyword", keyword);

        return "job-list";

    }

    @GetMapping("/view/{filename:.+}")
    public void viewFile(@PathVariable String filename, HttpServletResponse response) throws IOException {
        Path file = Paths.get(UPLOAD_DIR + filename);
        logger.info(">>>>>>>>>>>>>>>>>>>>>>Name of file at download " + filename);
        if (Files.exists(file)) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=" + filename);
            Files.copy(file, response.getOutputStream());
            response.getOutputStream().flush();
        }
    }

    @GetMapping("/images/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Resource file = new FileSystemResource(UPLOAD_DIR + filename);
        if (file.exists() && file.isReadable()) {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, MediaType.IMAGE_JPEG_VALUE).body(file);
        } else {
            return ResponseEntity.notFound().build();
        }


    }

    @PostMapping("/showFormForUpdate")
    public String updateJob(@RequestParam("jobId") Long theId, Model theModel, HttpServletRequest request) {

        //get the job from service
        Job theJobs = jobService.findById(theId);
        //set job in the model to prepolulate the form
        theModel.addAttribute("job", theJobs);
        //send over to our form

        HttpSession session = request.getSession();
        theModel.addAttribute("AuthId", (Long)session.getAttribute("AuthId"));
        return "job";
    }
    @PostMapping("/deleteJob")
    public String deleteJob(@RequestParam("jobId") Long theId){
        //delete the job
        jobService.deleteById(theId);
        //redirect to the list
        return "redirect:/jobs/list";
    }

    @PostMapping("/apply")
    public String applyForJob(@RequestParam Long jobId, HttpSession session, Model model) {
        try {
            // Get the job details
            Job job = jobService.findById(jobId);

            // Get the company email from the job details
            String companyEmail = job.getCompanyEmail();

            // Get the user details
            Long userId = (Long) session.getAttribute("AuthId");
            User user = userService.findById(userId);

            // Create and send the email
            sendApplicationEmail(user, job);

            // Set success attribute for Thymeleaf
            model.addAttribute("emailSentSuccess", true);
            model.addAttribute("emailSentError", false);

            return "redirect:/jobs/list";
        } catch (Exception e) {
            // Set error attribute for Thymeleaf
            model.addAttribute("emailSentSuccess", false);
            model.addAttribute("emailSentError", true);

            return "redirect:/jobs/list";
        }
    }



    private void sendApplicationEmail(User user, Job job) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(job.getCompanyEmail());
            message.setSubject("Job Application Received");
            message.setText("Hello,\n\n"
                    + "A user named " + user.getFirstName() + " " + user.getLastName() + " has applied for the job with title: " + job.getJobTitle() + ".\n\n"
                    + "Thank you.");

            javaMailSender.send(message);
        } catch (MailException e) {
            e.printStackTrace(); // Handle the exception according to your needs
        }
    }
}


