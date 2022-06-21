package htw.berlin.springcloud.services;

import htw.berlin.springcloud.persistence.AppUserEntity;
import htw.berlin.springcloud.persistence.IAppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class AppUserDetailsService implements UserDetailsService {
//public class AppUserDetailsService  {

    @Autowired
    private IAppUserRepository userRepository;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUserEntity userEntity = userRepository.findByEmail(username);

        if (userEntity == null){
            throw new UsernameNotFoundException("loadUserByUsername: Can't find user with username: " + username);
        }
        User user = new User(userEntity.getEmail(), userEntity.getPassword(), userEntity.isEnabled(), true, true, true, getAuthorities(Collections.singletonList(userEntity.getRole())));
        return user;
    }

    private Collection<? extends GrantedAuthority> getAuthorities(List<String> roles) {
        List<GrantedAuthority>  authorities = new ArrayList<>();
        for(String role: roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }
        return authorities;
    }
}
