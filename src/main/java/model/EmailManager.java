package model;

import email_service.EmailUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailManager {
    public Map<String, List<Email>> receivedEmail;
    private Map<String, List<Email>> sentEmail;
    public static int emailCount = 0;

    public EmailManager(){
        this.receivedEmail = new HashMap<>();
        this.sentEmail = new HashMap<>();
    }
    public boolean add(int id,String sender,String recipient, String subject,
                       String msg, LocalDateTime timestamp) {

        Email e  = new Email(id,sender,recipient,subject, msg, timestamp);
        boolean addedToSent = addToSentEmail(e);
        boolean addedToReceived = addToReceivedEmail(e);
        return addedToSent && addedToReceived;
    }
    public boolean addFirst(Email email){
        boolean  added = false;
        if(!sentEmail.containsKey(email.getSender())) {
            List<Email> sentByUser = new ArrayList<>();
            sentByUser.add(0, email);
            sentEmail.put(email.getSender(),sentByUser);
            added = true;
        }
        if(!receivedEmail.containsKey(email.getReceipiant())) {
            List<Email> receivedEmailsByUser = new ArrayList<>();
            receivedEmailsByUser.add(0, email);
            sentEmail.put(email.getReceipiant(),receivedEmailsByUser);
            added = true;
        }
        return added;
    }
    public boolean addToSentEmail(Email email){
        boolean  added = false;
        List<Email> senderSentEmails = sentEmail.getOrDefault(email.getSender(), new ArrayList<>());
        senderSentEmails.add(email);
        sentEmail.put(email.getSender(), senderSentEmails);
             added = true;
        return added;
    }
    public boolean addToReceivedEmail(Email email){
        boolean  added = false;
        /*if(receivedEmail.containsKey(email.getReceipiant())){
            List<Email> recievedByUser = new ArrayList<>();
            recievedByUser.add(email);
            receivedEmail.put(email.getReceipiant(),recievedByUser );
            added = true;
        }*/
        List<Email> recipientReceivedEmails = receivedEmail.getOrDefault(email.getReceipiant(), new ArrayList<>());
        recipientReceivedEmails.add(email);
        receivedEmail.put(email.getReceipiant(), recipientReceivedEmails);
            added = true;
        return added;
    }
    public List<Email> getSentEmailsForUser(String username) {
        return sentEmail.getOrDefault(username, new ArrayList<>());
    }
    public List<Email> getReceivedEmailsForUser(String username) {
        return receivedEmail.getOrDefault(username, new ArrayList<>());
    }


    public void initializeMailbox(String username) {
        receivedEmail.putIfAbsent(username, new ArrayList<>());
        sentEmail.putIfAbsent(username, new ArrayList<>());
    }


    public String sendEmail(String sender, String recipient, String subject, String content) {
        int emailId = ++emailCount;
        LocalDateTime now = LocalDateTime.now();
        boolean success = add(emailId, sender, recipient, subject, content, now);
        return success ? EmailUtilities.EMAIL_SENT : EmailUtilities.FAILED;
    }
    public List<Email> listSentEmailsByID(int id) {
        List<Email> matchingEmails = new ArrayList<>();
        // Iterate through all lists of received emails
        for (List<Email> emailList : sentEmail.values()) {
            for (Email email : emailList) {
                if (email.getId() == id) {
                    matchingEmails.add(email);
                }
            }
        }

        return matchingEmails;
    }
    public List<Email> listRecievedEmailsBySubject(String subject) {
        List<Email> matchingEmails = new ArrayList<>();
        // Iterate through all lists of received emails
        for (List<Email> emailList : receivedEmail.values()) {
            for (Email email : emailList) {
                if (email.getSubject().equalsIgnoreCase(subject)) {
                    matchingEmails.add(email);
                }
            }
        }

        return matchingEmails;
    }
    public List<Email> listRecievedEmailsBySender(String sender) {
        List<Email> matchingEmails = new ArrayList<>();
        // Iterate through all lists of received emails
        for (List<Email> emailList : receivedEmail.values()) {
            for (Email email : emailList) {
                if (email.getSender().equalsIgnoreCase(sender)) {
                    matchingEmails.add(email);
                }
            }
        }
        return matchingEmails;
    }


}
