package DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Role;
import entities.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserDTO {

    private final String USERNAME;
    private final String FULLNAME;
    private final List<RoleDTO> ROLES = new ArrayList<>();
    private final String CREATED;

    public UserDTO(User user) {
        this.USERNAME = user.getUserName();
        this.FULLNAME = user.getFirstName() + " " + user.getLastName();

        for (Role role : user.getRoles()) {
            ROLES.add(new RoleDTO(role));
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        CREATED = dateFormatter.format(user.getCreated());
    }

    @JsonProperty("userName")
    public String getUserName() {
        return USERNAME;
    }

    @JsonProperty("fullName")
    public String getFullName() {
        return FULLNAME;
    }

    @JsonProperty("joinedAt")
    public String getCreated() {
        return CREATED;
    }

    @JsonProperty("roles")
    public List<RoleDTO> getRoleList() {
        return ROLES;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.USERNAME);
        hash = 53 * hash + Objects.hashCode(this.FULLNAME);
        hash = 53 * hash + Objects.hashCode(this.ROLES);
        hash = 53 * hash + Objects.hashCode(this.CREATED);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final UserDTO other = (UserDTO) obj;
        if (!Objects.equals(this.USERNAME, other.USERNAME)) {
            return false;
        }
        if (!Objects.equals(this.FULLNAME, other.FULLNAME)) {
            return false;
        }
        if (!Objects.equals(this.CREATED, other.CREATED)) {
            return false;
        }
        if (!Objects.equals(this.ROLES, other.ROLES)) {
            return false;
        }
        return true;
    }

}
