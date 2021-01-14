package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Nikolaj Larsen
 */
@Entity
@Table(name = "ROLES")
@NamedQueries({
    @NamedQuery(name = "Role.deleteAllRows", query = "DELETE FROM Role"),
    @NamedQuery(name = "Role.getDefaults", query = "SELECT r FROM Role r WHERE r.defaultRole = TRUE")})
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ROLE_NAME", length = 20)
    private String roleName;

    @Column(name = "DEFAULT_ROLE")
    private boolean defaultRole = false;

    @ManyToMany(mappedBy = "roles")
    private List<User> userList;

    public Role(String roleName) {
        this.roleName = roleName;
        userList = new ArrayList<>();
    }

    public Role() {
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public void setDefaultRole(boolean condition) {
        defaultRole = condition;
    }

    public boolean getDefaultRole() {
        return defaultRole;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public void addUser(User user) {
        if (!userList.contains(user)) {
            userList.add(user);
            user.getRoles().add(this);
        }
    }

    public void removeUser(User user) {
        if (userList.contains(user)) {
            userList.remove(user);
            user.getRoles().remove(this);
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + Objects.hashCode(this.roleName);
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
        final Role other = (Role) obj;
        if (!Objects.equals(this.roleName, other.roleName)) {
            return false;
        }
        return true;
    }

}
