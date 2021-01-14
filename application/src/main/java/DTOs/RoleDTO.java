package DTOs;

import entities.Role;
import java.util.Objects;

/**
 *
 * @author Mathias Nielsen
 */
public class RoleDTO {

    private String roleName;

    public RoleDTO(Role role) {
        this.roleName = role.getRoleName();
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.roleName);
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
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        return true;
    }
    
}
