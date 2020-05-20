package com.example.email.def;

import com.example.email.pojo.EmailPojo;

public interface SendGridService {

    public String sendMail(EmailPojo emailPojo);

    String sendText1(EmailPojo emailPojo);
}
