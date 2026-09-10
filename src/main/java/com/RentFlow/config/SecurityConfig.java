 package com.RentFlow.config;

import com.RentFlow.security.jwt.JwtAuthenticationFilter;
import com.RentFlow.service.Impl.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            CustomUserDetailsService userDetailsService,
            JwtAuthenticationFilter jwtAuthenticationFilter) {

        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {

        auth.userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http)
            throws Exception {

    	http
        .cors()
        .and()
        .csrf().disable()

            .sessionManagement()
            .sessionCreationPolicy(
                    SessionCreationPolicy.STATELESS)

            .and()

            .authorizeRequests()

            // ==========================================
            // Authentication
            // ==========================================

            .antMatchers("/api/auth/login")
            .permitAll()

            .antMatchers("/api/auth/register")
            .permitAll()

            .antMatchers("/api/auth/change-password")
            .authenticated()
            .antMatchers("/api/profile/**").authenticated()
            
            .antMatchers("/api/admin/**")
            .hasRole("SUPER_ADMIN")

            .antMatchers("/api/organization/users/**")
            .hasRole("OWNER")
            
            // ==========================================
            // OWNER / MANAGER Dashboard
            // ==========================================

            .antMatchers("/api/dashboard/owner")
            .hasAnyRole("OWNER", "MANAGER")


            .antMatchers("/api/maintenance/**")
            .hasAnyRole("OWNER", "TENANT")
            
            // ==========================================
            // TENANT Dashboard
            // ==========================================

            .antMatchers("/api/dashboard/tenant")
            .hasRole("TENANT")
            
           


            // ==========================================
            // Property APIs
            // OWNER only
            // ==========================================

            .antMatchers("/api/properties/**")
            .hasRole("OWNER")


            // ==========================================
            // Tenant - Own Profile
            // ==========================================

            .antMatchers("/api/tenants/me/**")
            .hasRole("TENANT")


            // ==========================================
            // Tenant Management
            // OWNER / MANAGER
            // ==========================================

            .antMatchers("/api/tenants/**")
            .hasAnyRole("OWNER", "MANAGER")


            // ==========================================
            // Tenant - Own Lease
            // ==========================================

            .antMatchers("/api/leases/my/**")
            .hasRole("TENANT")


            // ==========================================
            // Lease Management
            // OWNER / MANAGER
            // ==========================================

            .antMatchers("/api/leases/**")
            .hasAnyRole("OWNER", "MANAGER")


            // ==========================================
            // Tenant - Own Payments
            // ==========================================

            .antMatchers("/api/payments/my/**")
            .hasRole("TENANT")


            // ==========================================
            // Payment Management
            // OWNER / MANAGER
            // ==========================================

            .antMatchers("/api/payments/**")
            .hasAnyRole("OWNER", "MANAGER")

            .antMatchers("/api/notifications/**")
            .hasAnyRole("OWNER", "MANAGER", "TENANT")

            // ==========================================
            // Everything else
            // ==========================================

            .anyRequest()
            .authenticated()

            .and()

            .addFilterBefore(
                    jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean()
            throws Exception {

        return super.authenticationManagerBean();
    }
}