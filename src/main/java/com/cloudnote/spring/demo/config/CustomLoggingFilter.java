package com.cloudnote.spring.demo.config;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;


public class CustomLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        System.out.println("_________________________________________________________________________________");
        System.out.println("CustomLoggingFilter - Request URI: " + request.getRequestURI());
        filterChain.doFilter(request, response);
        System.out.println("CustomLoggingFilter - Response Status: " + response.getStatus());
        System.out.println("_________________________________________________________________________________");

    }
}
