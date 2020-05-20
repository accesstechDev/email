package com.example.email.imp;

import com.example.email.def.SendGridService;
import com.example.email.pojo.EmailPojo;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class SendGridServiceImpl implements SendGridService {

    final private String sendGridApi = "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";

    /**
     * PersonalizeEmail - details setting for each email. For the complete example:
     * https://github.com/sendgrid/sendgrid-java/blob/master/examples/helpers/mail/Example.java
     *
     * @param emailPojo
     * @return Mail
     */
    private Mail PersonalizeEmail(EmailPojo emailPojo) {

        Mail mail = new Mail();

        /* From information setting */
        Email fromEmail = new Email();
        fromEmail.setName(emailPojo.getFromName());
        fromEmail.setEmail(emailPojo.getFromEmail());

        mail.setFrom(fromEmail);
        mail.setSubject(emailPojo.getEmailSubject());

        /*
         * Personalization setting, we only add recipient info for this particular
         * example
         */
        Personalization personalization = new Personalization();
        Email to = new Email();
        to.setName(emailPojo.getToName());
        to.setEmail(emailPojo.getToEmail());
        personalization.addTo(to);

        personalization.addHeader("X-Test", "test");
        personalization.addHeader("X-Mock", "true");

        /* Substitution value settings */
        personalization.addDynamicTemplateData("%name%", emailPojo.getToName());
        personalization.addDynamicTemplateData("%from%", emailPojo.getFromName());
        personalization.setSubject(emailPojo.getEmailSubject());

        mail.addPersonalization(personalization);

        /* Set template id */
        mail.setTemplateId("d-b5a76c216d5d46b39e18b5d110fd919a");

        /* Reply to setting */
        Email replyTo = new Email();
        replyTo.setName(emailPojo.getFromName());
        replyTo.setEmail(emailPojo.getFromEmail());
        mail.setReplyTo(replyTo);

        /* Adding Content of the email */
        Content content = new Content();

        /* Adding email message/body */
        content.setType("text/plain");
        content.setValue(emailPojo.getMessage());
        mail.addContent(content);

        return mail;
    }


    public String sendMail(EmailPojo emailPojo) {

        SendGrid sg = new SendGrid(sendGridApi);
        sg.addRequestHeader("X-Mock", "true");

        Request request = new Request();
        Mail mail = PersonalizeEmail(emailPojo);
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            ex.printStackTrace();
            return "Failed to send mail! " + ex.getMessage();
        }
        return "Email is sent Successfully!!";
    }



    public String sendText1(EmailPojo pojo) {
        Response response = sendEmail1(pojo.getFromEmail(), pojo.getToEmail(), pojo.getEmailSubject(), new Content("text/plain", pojo.getMessage()));
        System.out.println("Status Code: " + response.getStatusCode() + ", Body: " + response.getBody() + ", Headers: "
                + response.getHeaders());
        return "success";
    }

    private Response sendEmail1(String from, String to, String subject, Content content) {
        Mail mail = new Mail(new Email(from), subject, new Email(to), content);
        mail.setReplyTo(new Email(to));
        SendGrid sg = new SendGrid(sendGridApi);
        sg.addRequestHeader("X-Mock", "true");
        Request request = new Request();
        Response response = null;
        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            response = sg.api(request);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        return response;
    }
}
