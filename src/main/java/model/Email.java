package model;
import lombok.*;
import org.w3c.dom.Text;

import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class Email {
    //Class properties
    @EqualsAndHashCode.Include
    private int id;
    private String sender;
    private String receipiant;
    private String subject;
    //private Text message;
    private String message;
    private LocalDateTime timeStamp;

}
