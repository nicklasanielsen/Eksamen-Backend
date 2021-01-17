package DTOs;

import com.fasterxml.jackson.annotation.JsonProperty;
import entities.Role;
import java.util.Objects;

/**
 *
 * @author Mathias Nielsen
 */
public class RoleDTO {

    private final String ROLE_NAME;

    public RoleDTO(Role role) {
        ROLE_NAME = role.getRoleName();
    }

    @JsonProperty("roleName")
    public String getRoleName() {
        return ROLE_NAME;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 61 * hash + Objects.hashCode(this.ROLE_NAME);
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
        final RoleDTO other = (RoleDTO) obj;
        if (!Objects.equals(this.ROLE_NAME, other.ROLE_NAME)) {
            return false;
        }
        return true;
    }

}
