package security;

import entities.User;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Nicklas Nielsen
 */
public class UserPrincipal implements Principal {

    private String username;
    private List<String> roles = new ArrayList<>();

    /* Create a UserPrincipal, given the Entity class User*/
    public UserPrincipal(User user) {
        username = user.getUserName();

        user.getRoles().forEach(role -> {
            roles.add(role.getRoleName());
        });
    }

    public UserPrincipal(String username, String[] roles) {
        this.username = username;
        this.roles = Arrays.asList(roles);
    }

    @Override
    public String getName() {
        return username;
    }

    public boolean isUserInRole(String role) {
        return roles.contains(role);
    }

}
