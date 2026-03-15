package com.movietickets.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        // pega o header Authorization da requisição
        String header = request.getHeader("Authorization");

        // se não tiver header ou não começar com "Bearer ", deixa passar sem autenticar
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        // remove o prefixo "Bearer " e fica só com o token
        String token = header.substring(7);

        // valida o token
        boolean tokenValido = jwtService.isValid(token);

        if (tokenValido) {
            // extrai o email do token
            String email = jwtService.extractEmail(token);

            // cria o objeto de autenticação do Spring Security
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());

            // registra o usuário como autenticado no contexto da requisição
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // continua o fluxo da requisição
        chain.doFilter(request, response);
    }
}