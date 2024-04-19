package poly.edu.security;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import poly.edu.entity.User;
import poly.edu.repository.UserRepository;

@Service
public class CustomAccountDetailService implements UserDetailsService {

    @Autowired
    private UserRepository accountService;

    @Override
public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User account = accountService.findByUsername(username);
    if (account == null) {
        throw new UsernameNotFoundException("Không tồn tại");
    }

    List<String> roles = Collections.singletonList(account.getRole());

    // Lấy vai trò từ trường role của người dùng và tạo một GrantedAuthority
    List<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role))
            .collect(Collectors.toList());

    return new CustomAccountDetails(account, authorities);
}

}
