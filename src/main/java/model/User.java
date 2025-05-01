package model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString

public class User {
    //Class Properties
    private String username;
    private String password;
    private List<Email> sentEmails;
    private List<Email> receivedEmails;
}
