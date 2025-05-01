package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailManager {
    private Map<String, List<Email>> receivedEmail;
    private Map<String, List<Email>> sentEmail;

    public EmailManager(){
        this.receivedEmail = new HashMap<>();
        this.sentEmail = new HashMap<>();
    }
    public boolean addToSentEmail(Email email){
        boolean  added = false;
        if(sentEmail.containsKey(email.getSender())){
            List<Email> sentByUser = new ArrayList<>();
            sentByUser.add(email);
            sentEmail.put(email.getSender(),sentByUser );
            added = true;
        }
        return added;
    }
    public boolean addToReceevedEmail(Email email){
        boolean  added = false;
        if(receivedEmail.containsKey(email.getReceipiant())){
            List<Email> recievedByUser = new ArrayList<>();
            recievedByUser.add(email);
            receivedEmail.put(email.getReceipiant(),recievedByUser );
            added = true;
        }
        return added;

    }

}
