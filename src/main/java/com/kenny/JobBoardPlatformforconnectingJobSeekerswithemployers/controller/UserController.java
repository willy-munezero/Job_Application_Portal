package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.controller;


import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.dao.UserDao;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.model.User;
import com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.data.repository.query.Param;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;
import java.util.logging.Logger;



@Controller
@RequestMapping("/Manager")
public class UserController {

    //@Autowired
    private UserService userService;
    private UserDao userDao;
   // @Autowired
    //private ManagerService managerService;

    private JavaMailSender mailSender;


    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Autowired
    public UserController(UserService userService, UserDao userDao, JavaMailSender mailSender) {
        this.userService = userService;
        this.userDao = userDao;
        this.mailSender = mailSender;
    }




    private Logger logger = Logger.getLogger(getClass().getName());

    // add an initbinder ... to convert trim input strings
    // remove leading and trailing whitespace
    // resolve issue for our validation
    @InitBinder
    public void initBinder(WebDataBinder dataBinder)
    {
        StringTrimmerEditor stringTrimmerEditor = new StringTrimmerEditor(true);

        dataBinder.registerCustomEditor(String.class, stringTrimmerEditor);
    }


    @GetMapping("/show")
    public String showMyLoginPage(Model theModel) {
        User user = new User();
        theModel.addAttribute("user", user);
        return "registration-form";
    }

    @PostMapping("/processForm")
    public String processForm(
            @Valid @ModelAttribute("user") User user,
            BindingResult theBindingResult,
            Model theModel) {

        if(theBindingResult.hasErrors()){
            return "registration-form";
        }
        String userName = user.getUsername();
        logger.info("Processing registration form for: " + userName
        + " With role = " + user.getFormRole());

        // check the database if user already exists
        User existing = userService.findByUserName(userName);
        if (existing != null){
           // theModel.addAttribute("manager",  new OperationsManager());
            theModel.addAttribute("registrationError", "User name already exists.");
            logger.warning("User name already exists.");
            return "registration-form";
        }
        //
        //We passed all of the validation checks!
        // let's get down to business!!!
        //

        // encrypt the password
        //String encodedPassword = passwordEncoder.encode(operationsManager.getPassword());

        // prepend the encoding algorithm id
       // encodedPassword = "{bcrypt}" + encodedPassword;

        // give user default role of "inspector"
        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList();
        authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYEER"));

        // if the user selected role other than employee,
        // then add that one too (multiple roles)
        String formRole = user.getFormRole();

        if (!formRole.equals("ROLE_EMPLOYEER")) {
            if (formRole.equals("ROLE_JOB_SEEKER")) {
                authorities.add(new SimpleGrantedAuthority(formRole));
            }else if(formRole.equals("ROLE_OPERATIONS_MANAGER")){
                authorities.add(new SimpleGrantedAuthority("ROLE_JOB_SEEKER"));
                authorities.add(new SimpleGrantedAuthority(formRole));
            }
        }
        logger.info("=========roles========");
        for(GrantedAuthority auth:authorities){
            logger.info(auth.toString() + "\n");
        }

        // create user account
        userService.save(user, authorities);

        logger.info("Successfully created user: " + userName);

        theModel.addAttribute("username", userName);

        return "registration-success";
    }
    @GetMapping("/loadForgotPassword")
    public String loadForgotPassword(){
      return "forgot-password";
    }

    @GetMapping("/loadResetPassword/{id}")
    public String loadResetPassword(@PathVariable Long id, Model m){

        m.addAttribute("id",id);
        return "reset-password";
    }
    @PostMapping("/forgotPassword")
    public String forgotPassword(@RequestParam String email, @RequestParam String phoneNumber, HttpSession session){
        User usr = userService.findByEmailAndPhoneNumber(email,phoneNumber);

        if (usr != null){
            return "redirect:/Manager/loadResetPassword/" + usr.getId();
//            return "redirect:/loadResetPassword";
        }else {
            session.setAttribute("msg", "Invalid email and Phone Number");
            return "forgot-password";
        }
   }
     @PostMapping("/changePassword")
    public String resetPassword(@RequestParam String psw, @RequestParam Long id, HttpSession session){

        User usr = userService.findById(id);
        String encryprPsw=passwordEncoder.encode(psw);
        usr.setPassword(encryprPsw);

        User updateUser =userService.save(usr);

        if(updateUser!=null){
            session.setAttribute("msg","Password changed successfully");
        }
        return  "redirect:/showMyLoginPage";
    }
    @GetMapping("/listUsers")
    public String getAllUsers(Model theModel)
    {
        String keyword = null;
        return getUsersPage(0, 2, keyword, theModel);
    }
    @GetMapping("/users/{pageNumber}")
    public String getUsersPage(
//            @RequestParam(defaultValue = "0") int page,
            @PathVariable("pageNumber") int page,
            @RequestParam(defaultValue = "2") int size,
            @Param("keyword") String keyword,
            Model model) {

        //List<User> users = userDao.findUsers(page, size);
        List<User> users = userDao.findUsers(page, size,keyword);
        long totalUsers = userDao.countUsers();
        int totalPages = (int) Math.ceil((double) totalUsers / size);
        for(User user:users)
        {
            logger.info(user.toString());
        }
        logger.info("currentPage" + page);
        logger.info("totalPages" + totalPages);

        model.addAttribute("currentPage", page+1);
        model.addAttribute("TotalItems", totalUsers);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("users", users);
        model.addAttribute("keyword", keyword);
        return "usersList";
    }
    @PostMapping("/approve")
    public String approveUser(@RequestParam("userId") Long userId, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user != null) {
            // Send approval email without updating user or setting status
            boolean emailSent = sendApprovalEmail(user.getEmail());

            if (emailSent) {
                redirectAttributes.addAttribute("success", true);
            } else {
                redirectAttributes.addAttribute("error", true);
            }
        }

        return "redirect:/Manager/users/0";
    }

    @PostMapping("/reject")
    public String rejectUser(@RequestParam("userId") Long userId, Model model, RedirectAttributes redirectAttributes) {
        User user = userService.findById(userId);
        if (user != null) {
            // Send rejection email without updating user or setting status
            boolean emailSent = sendRejectionEmail(user.getEmail());

            if (emailSent) {
                redirectAttributes.addAttribute("success", true);
            } else {
                redirectAttributes.addAttribute("error", true);
            }
        }

        return "redirect:/Manager/users/0";
    }

    private boolean sendApprovalEmail(String recipientEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Account Approved");
            message.setText("Your account has been approved. You can now log in to the system.");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
            return false;
        }
    }

    private boolean sendRejectionEmail(String recipientEmail) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(recipientEmail);
            message.setSubject("Account Rejected");
            message.setText("Your account has been rejected. If you have any questions, please contact support.");
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception as needed
            return false;
        }
    }
}
