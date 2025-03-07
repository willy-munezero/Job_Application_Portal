package com.kenny.JobBoardPlatformforconnectingJobSeekerswithemployers.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/email")
public class MailController {

    @Autowired
    private JavaMailSender mailSender;

    @GetMapping(value = "/email-attachment")
    public String emailWithAttachment() {


        return "email-attachment";
    }

    @PostMapping("/sendEmailWithAttachment")
//    @ResponseBody
//@RequestMapping(value = "/sendEmailWithAttachment", method = RequestMethod.POST)
    public String sendEmailWithAttachment(HttpServletRequest request,final @RequestParam("attachFile") MultipartFile[] attachFile) {

        final String name = request.getParameter("name");
        final String emailTo = request.getParameter("mailTo");
        final String subject = request.getParameter("subject");
        final String message = request.getParameter("message");

        //for multiple recipients
        String[] to = emailTo.split(",");

        //for logging
        System.out.println("name: " + name);
        System.out.println("emailTo: " + emailTo);
        System.out.println("Subject: " + subject);
        System.out.println("message: " + message);

        List<MultipartFile> attachments = new ArrayList<>();
        for (MultipartFile file : attachFile) {
            System.out.println("Attach File: " + file.getOriginalFilename());
            attachments.add(file);
        }
        System.out.println("sending.....");
        mailSender.send(new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                //for multiple recipients
                if (!to.equals("")) {
                    messageHelper.setTo(to);
                }
                //message helper.set(Email to);
                messageHelper.setSubject(subject);
                messageHelper.setText(message);
                //determines if there is an upload file, attach it to the email

                if (!attachments.isEmpty()) {
                    for (MultipartFile file : attachments) {
                        messageHelper.addAttachment(file.getOriginalFilename(), file);
                    }
                }
                System.out.println("Sending done");
            }

        });
        return "success";
    }

}
