package DTOs;

import entities.Role;
import entities.User;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Mathias Nielsen
 */
public class UserDTO {

    private String userName;
    private String fullName;
    private List<RoleDTO> roleList = new ArrayList<>();
    private String created;

    public UserDTO(User user) {
        this.userName = user.getUserName();
        this.fullName = user.getFirstName() + " " + user.getLastName();

        for (Role role : user.getRoles()) {
            roleList.add(new RoleDTO(role));
        }

        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        created = dateFormatter.format(user.getCreated());
    }

    public String getUserName() {
        return userName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getCreated() {
        return created;
    }

    public List<RoleDTO> getRoleList() {
        return roleList;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.userName);
        hash = 53 * hash + Objects.hashCode(this.fullName);
        hash = 53 * hash + Objects.hashCode(this.roleList);
        hash = 53 * hash + Objects.hashCode(this.created);
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
        if (!Objects.equals(this.userName, other.userName)) {
            return false;
        }
        if (!Objects.equals(this.fullName, other.fullName)) {
            return false;
        }
        if (!Objects.equals(this.roleList, other.roleList)) {
            return false;
        }
        return true;
    }

}
