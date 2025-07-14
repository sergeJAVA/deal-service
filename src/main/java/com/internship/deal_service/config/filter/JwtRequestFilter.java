package com.internship.deal_service.config.filter;

import com.internship.deal_service.model.security.TokenAuthentication;
import com.internship.deal_service.service.security.JwtService;
import com.internship.deal_service.util.TokenUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * {@code JwtRequestFilter} — это компонент Spring, который фильтрует входящие HTTP-запросы
 * для проверки JWT-токенов и настройки контекста Spring Security.
 * <p>
 * Он расширяет {@link OncePerRequestFilter}, чтобы гарантировать выполнение только один раз за запрос.
 * Этот фильтр извлекает JWT-токен из запроса, проверяет его, и если он действителен и не истек,
 * аутентифицирует пользователя, устанавливая {@link TokenAuthentication} в {@link SecurityContextHolder}.
 * Если токен истек, он отправляет HTTP-ответ с ошибкой 401 Unauthorized.
 * </p>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = TokenUtil.parseToken(request);

        if (Optional.ofNullable(token).isPresent()) {
            if (jwtService.isTokenExpired(token)) {
                sendErrorResponse(response, "Token has expired. Please log in again!", HttpStatus.UNAUTHORIZED);
                log.info("Token has expired. Please log in again!");
                return;
            }
            TokenAuthentication authentication = new TokenAuthentication(jwtService.parseToken(token));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private void sendErrorResponse(HttpServletResponse response,
                                   String message,
                                   HttpStatus status)
            throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

}
