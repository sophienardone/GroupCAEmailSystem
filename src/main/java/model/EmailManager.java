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


    /**
     * Adds a new email to both the sender's sent emails and the recipient's received emails.
     *
     * @param id        the unique ID of the email
     * @param sender    the username of the sender
     * @param recipient the username of the recipient
     * @param subject   the subject line of the email
     * @param msg       the body content of the email
     * @param timestamp the time the email was sent
     * @return true if the email was successfully added to both sent and received lists, false otherwise
     */

    public boolean add(int id,String sender,String recipient, String subject,
                       String msg, LocalDateTime timestamp) {

        Email e  = new Email(id,sender,recipient,subject, msg, timestamp);
        boolean addedToSent = addToSentEmail(e);
        boolean addedToReceived = addToReceivedEmail(e);
        return addedToSent && addedToReceived;
    }



    /**
     * Adds an email to the sender's sent mailbox and the recipient's received mailbox
     * only if their lists do not already exist. The email is inserted at the
     * beginning of the list to represent most recent first.
     *
     * @param email the Email object to be added
     * @return true if the email was added to either sent or received lists; false if neither list was updated
     */
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
            receivedEmail.put(email.getReceipiant(), receivedEmailsByUser);
            added = true;
        }
        return added;
    }

    /**
     * Adds the given email to the sender's list of sent emails.
     * If the sender has no previous sent emails, a new list is created.
     * The email is then added to this list, and the map is updated.
     *
     * @param email the Email object to be added to the sender's sent mailbox
     * @return true if the email was successfully added
     */

    public boolean addToSentEmail(Email email){
        boolean  added = false;
        List<Email> senderSentEmails = sentEmail.getOrDefault(email.getSender(), new ArrayList<>());
        senderSentEmails.add(email);
        sentEmail.put(email.getSender(), senderSentEmails);
             added = true;
        return added;
    }


    /**
     * Adds the given email to the recipient's list of received emails.
     * If the recipient has no existing entry, a new list is created and updated in the map.
     * The email is then added to this list to reflect it in the recipient's inbox.
     *
     * @param email the Email object to be added to the recipient's received mailbox
     * @return true if the email was successfully added
     */

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

    /**
     * Initializes empty mailboxes for a new user.
     * Creates entries for both received and sent email lists if they do not already exist.
     *
     * @param username the username for whom the mailboxes should be for
     */

    public void initializeMailbox(String username) {
        receivedEmail.putIfAbsent(username, new ArrayList<>());
        sentEmail.putIfAbsent(username, new ArrayList<>());
    }

    /**
     * Sends an email by generating a unique ID and timestamp, then adding it to both
     * the sender's sent emails and the recipient's received emails.
     *
     * @param sender the username of the sender
     * @param recipient the username of the recipient
     * @param subject the subject of the email
     * @param content the content/message of the email
     * @return returns either success (EMAIL_SENT) or failure (FAILED)
     */

    public String sendEmail(String sender, String recipient, String subject, String content) {
        int emailId = ++emailCount;
        LocalDateTime now = LocalDateTime.now();
        boolean success = add(emailId, sender, recipient, subject, content, now);
        return success ? EmailUtilities.EMAIL_SENT : EmailUtilities.FAILED;
    }

    /**
     * Searches through all sent emails and returns a list of emails
     * that match the provided email ID.
     *
     * @param id the unique ID of the email to search for
     * @return a list of sent emails with the specified ID, or an empty list if none found
     */

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

    /**
     * Retrieves all received emails that have a subject matching the specified keyword.
     *
     * @param subject the subject keyword to search for which is case-insensitive
     * @return a list of received emails with a matching subject, or an empty list if none match
     */

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


    /**
     * Retrieves all received emails that were sent by the specified sender.
     *
     * @param sender the username of the sender to search for which is case-insensitive
     * @return a list of received emails from the specified sender, or an empty list if none match
     */

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
