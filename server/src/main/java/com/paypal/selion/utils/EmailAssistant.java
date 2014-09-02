/*-------------------------------------------------------------------------------------------------------------------*\
|  Copyright (C) 2014 eBay Software Foundation                                                                        |
|                                                                                                                     |
|  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance     |
|  with the License.                                                                                                  |
|                                                                                                                     |
|  You may obtain a copy of the License at                                                                            |
|                                                                                                                     |
|       http://www.apache.org/licenses/LICENSE-2.0                                                                    |
|                                                                                                                     |
|  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed   |
|  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for  |
|  the specific language governing permissions and limitations under the License.                                     |
\*-------------------------------------------------------------------------------------------------------------------*/

package com.paypal.selion.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * A utility method that helps sending out "text" mails using the SMTP Host.
 * 
 * @deprecated - This utility is being deprecated because the need for email notifications may no longer be a
 *             requirement.
 * 
 */
@Deprecated
public class EmailAssistant {

    private EmailAssistant() {

    }

    /**
     * Helps sending an email.
     * 
     * @param mailContent
     *            - The content of the e-mail.
     * @param toAddress
     *            - The Address to which the mail is to be sent.
     * @param subject
     *            - The subject of the e-mail.
     */
    public static void sendMail(String mailContent, String toAddress, String subject) {

        Properties props = new Properties();
        props.put("mail.smtp.host", "atom.corp.ebay.com");
        Session session = Session.getInstance(props);
        try {
            Message message = new MimeMessage(session);
            InternetAddress fromAddress = new InternetAddress("do-not-reply-AutoBot-daemon@paypal.com");
            fromAddress.setPersonal("Autobot Notification");
            message.setFrom(fromAddress);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toAddress));
            message.setSubject(subject);
            message.setText(mailContent);
            Transport.send(message);
        } catch (Exception e) {// NOSONAR
            // We are only attempting to send an email. If it fails, we should not report it.
        }
    }
}