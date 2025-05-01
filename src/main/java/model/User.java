package model;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString

public class User {
    //Class Properties
    @EqualsAndHashCode.Include
    private String username;
    private String password;

}
