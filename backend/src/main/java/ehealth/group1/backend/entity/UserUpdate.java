package ehealth.group1.backend.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter @Setter
public class UserUpdate extends User {
    private String oldName;
    private String oldPassword;
}
