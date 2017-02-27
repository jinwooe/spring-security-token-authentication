package jade;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepository {
    private static Map<String, CustomUser> users = new HashMap<>();
    static {
        users.put("user", new CustomUser("user", "password"));
    }
    Optional<CustomUser> findByUsername(String username) {
        CustomUser customUser = users.get(username);
        return Optional.ofNullable(customUser);
    }
}
