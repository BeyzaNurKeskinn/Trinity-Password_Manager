
package com.project.Trinity.Config;

import com.project.Trinity.Filter.JwtAuthenticationFilter;
import com.project.Trinity.Filter.JwtAuthorizationFilter;
import com.project.Trinity.Service.RefreshTokenService;
import com.project.Trinity.Service.UserService;
import com.project.Trinity.Util.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;//Kullanıcı bilgilerini yüklemek için.
    private final JwtUtil jwtUtil;//JWT oluşturma ve doğrulama için
    private final PasswordEncoder passwordEncoder;//Şifre doğrulama için.
//Bu bağımlılıklar, kimlik doğrulama ve yetkilendirme için gerekli.
    
    public SecurityConfig(UserService userService, JwtUtil jwtUtil, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }//Constructor injection ile bağımlılıkları enjekte eder.

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {//Veritabanı tabanlı kimlik doğrulama sağlar.
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);//Kullanıcı bilgilerini UserService’ten alır.
        authProvider.setPasswordEncoder(passwordEncoder);//Şifreleri doğrulamak için BCryptPasswordEncoder’ı kullanır.
        return authProvider;
    }//Kullanıcı adı ve şifreyi veritabanıyla karşılaştırmak için. Spring Security, bu provider’ı login sırasında kullanır.

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }//Kimlik doğrulama işlemlerini yönetir (örneğin, kullanıcı adı/şifre kontrolü).
    //Login ve JWT doğrulama için gerekli. JwtAuthenticationFilter bunu kullanır.

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        return new JwtAuthenticationFilter(authenticationManager, jwtUtil, refreshTokenService);
    }//Özel JwtAuthenticationFilter’ı oluşturur ve bağımlılıklarını enjekte eder.
    //Neden?: Login sırasında JWT oluşturmak için. Bu filter, /api/auth/login endpoint’ini işler.

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil, userService);//Her istekte JWT’yi doğrulamak için. Yetkili endpoint’lere erişimi kontrol eder.
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Kimlik doğrulaması gerektirmeyen endpoint'ler
                .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/refresh-token", "/api/auth/forgot-password", "/api/auth/reset-password","/api/auth/**","/health", "/error").permitAll()
                // /api/auth/user/** için kimlik doğrulaması gerekli
                .requestMatchers("/api/auth/user/**").authenticated()
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .requestMatchers("/api/protected/admin").hasAuthority("ADMIN")
                .requestMatchers("/api/protected/user").authenticated()
                .requestMatchers("/api/user/me").authenticated()
                .requestMatchers("/api/user/**").hasAnyAuthority("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterAt(jwtAuthenticationFilter(authenticationManager, jwtUtil, refreshTokenService), UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
       configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "https://trinity-frontend-szj7.onrender.com"));
        configuration.addAllowedMethod("*"); // GET, POST, OPTIONS, vb.
        configuration.addAllowedHeader("*"); // Tüm başlıklar
        configuration.setAllowCredentials(true); // Çerez veya kimlik doğrulama için
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Tüm endpoint'ler için
        return source;
    }
}
 
