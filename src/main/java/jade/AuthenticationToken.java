package jade;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@AllArgsConstructor
@Getter
@Setter
public class AuthenticationToken {
    private String username;
    private Collection<? extends GrantedAuthority> authorities;
    private String token;
}
