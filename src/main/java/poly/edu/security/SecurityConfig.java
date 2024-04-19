package poly.edu.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;



@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @SuppressWarnings("removal")
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests((auth) -> auth
                    .requestMatchers("/*", "/api/**","/admin/**","/order/**").permitAll()
                    .requestMatchers("/user/**").hasAuthority("USER")
                    .requestMatchers("/admin/**", "/employee/**", "/api/telegram/approve/**").hasAuthority("ADMIN")
                    .requestMatchers("/employee/**").hasAuthority("EMPLOYEE")
                    .anyRequest().authenticated())
            .formLogin(login -> login.loginPage("/login").loginProcessingUrl("/login")
                    .usernameParameter("username").passwordParameter("password")
                    .defaultSuccessUrl("/default", true)
                    .failureHandler(authenticationFailureHandler()) // Sử dụng authenticationFailureHandler()
                    .failureUrl("/login?error=true"))
            
            .logout((logout) -> logout.permitAll());

        http.exceptionHandling().accessDeniedPage("/denied-page");
        return http.build();
    }

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers("/css/**", "/fonts/**", "/images/**", "/js/**", 
        "/assets/**", "/dist/**", "/taiNguyenCuaThang/**", "/taiNguyenCuaDung/**");
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler("/index"); // Trả về trang index nếu authentication thất bại
    }
}

