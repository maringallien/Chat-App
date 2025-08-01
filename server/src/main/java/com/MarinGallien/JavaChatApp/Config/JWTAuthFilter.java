package com.MarinGallien.JavaChatApp.Config;

import com.MarinGallien.JavaChatApp.Database.JPAEntities.User;
import com.MarinGallien.JavaChatApp.Database.JPARepositories.UserRepo;
import com.MarinGallien.JavaChatApp.Services.AuthService.JWTService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JWTAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTAuthFilter.class);

    private JWTService jwtService;
    private UserRepo userRepo;

    public JWTAuthFilter(JWTService jwtService, UserRepo userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        // Extract token from authorization header
        String authHeader = request.getHeader("Authorization");
        String jwt = jwtService.extractTokenFromHeader(authHeader);

        // If no token present, continue without authentication
        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Validate token
            if (!jwtService.validateToken(jwt)) {
                logger.warn("Invalid JWT token in request");
                filterChain.doFilter(request, response);
                return;
            }

            // Extract user information from token
            String userId = jwtService.extractUserId(jwt);

            // Check if user is already authenticated in this request
            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load user from database
                User user = userRepo.findUserById(userId);

                if (user != null) {
                    // Create authentication token
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user.getUserId(),
                            null,
                            new ArrayList<>()
                    );

                    // Set additional details
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set authentication in security context
                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    logger.debug("Successfully authenticated user: {}", userId);
                } else {
                    logger.warn("User not found in database for userId: {}", userId);
                }
            }

        } catch (Exception e) {
            logger.error("Error processing JWT token: {}", e.getMessage());
            // Continue without authentication
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // Skip JWT filter for authentication endpoints and public endpoints
        return path.equals("/auth/login") ||
                path.equals("/auth/register") ||
                path.equals("/h2-console") ||
                path.equals("/public") ||
                path.startsWith("/ws");
    }

}
