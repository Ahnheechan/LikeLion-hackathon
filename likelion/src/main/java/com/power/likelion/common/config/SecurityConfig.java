package com.power.likelion.common.config;


import com.power.likelion.filter.JwtAuthenticationFilter;
import com.power.likelion.utils.jwts.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // ID, Password 문자열을 Base64로 인코딩하여 전달하는 구조
                .httpBasic().disable()
                // 쿠키 기반이 아닌 JWT 기반이므로 사용 X
                .csrf().disable()
                //cors 설정 --> 나중에 오류로 고생할 수있음
                .cors().configurationSource(corsConfigurationSource())
                .and()
                // Spring Security 세션 정책 : 세션을 생성 및 사용하지 않음
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // 조건별로 요청 허용/제한 설정
                .authorizeRequests()

                //API 명세서 관련된 모든 요소들도 모두 승인
                .antMatchers("/", "/swagger-ui/**", "/v3/**","/swagger-ui.html").permitAll()
                // 회원가입과 로그인은 모두 승인
                .antMatchers("/login", "/sign-up","/admin/sign-up").permitAll()   // permitAll()을 하게되면 JWT 필터를 거치지 않고 간다.
                //질문 하나를 보는건 비회원도 접근가능 but 질문 생성, 삭제, 답변작성 등은 회원만 가능
                .antMatchers(HttpMethod.GET,"/questions","/questions/{id}").permitAll()
                .antMatchers(HttpMethod.GET,"/boards","/boards/{id}").permitAll()

                .antMatchers("/questions/create").hasRole("USER")
                // /admin으로 시작하는 요청은 ADMIN 권한이 있는 유저에게만 허용
                .antMatchers("/admin/**").hasRole("ADMIN")
                // /user 로 시작하는 요청은 USER 권한이 있는 유저에게만 허용
                .antMatchers("/user/**").hasRole("USER")

                //로컬에서 소셜 로그인 테스트 용
                .antMatchers("/login/oauth2/**").permitAll()
                .antMatchers("/oauth2/**","/social/**").permitAll()

                // AI 소개 게시판 조회
                .antMatchers("/ai-info","/ai-info/**").permitAll()
                //이미지 업로드는 모두 허용
                .antMatchers("/upload/**").permitAll()

                // 카카오 페이
                .antMatchers("/payment/**").permitAll()

                .anyRequest().authenticated(); // 위에서 설정한 API를 제외하고는 모두 JWT 필터를 거친다는 소리 TODO 나중에 바꿔야 함


        /** 아래 내용은 인증과 권한을 확인하는 단계이다. 일반 회원가입으로 로그인을 진행하면 로그인시 제공한 JWT 토큰을 확인하여 아래 과정이 일어나고
         *  OAuth2 로그인시 SuccessHandler에서 발급한 토큰을 이용하여 아래 작용이 일어나게 된다. */
        http
                .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)

                // JWT 인증 필터 적용

                // 에러 핸들링
                .exceptionHandling()
                .accessDeniedHandler(new AccessDeniedHandler() { // 권한이 없는경우 처리 즉 인증은 됬지만 role의 권한이 없는 것
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
                        // 권한 문제가 발생했을 때 이 부분을 호출한다.
                        response.setStatus(403);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("권한이 없는 사용자입니다.");
                    }
                })
                .authenticationEntryPoint(new AuthenticationEntryPoint() { // 인증이 안된 사용자를 예외처리 하는 것
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
                        // 인증문제가 발생했을 때 이 부분을 호출한다.
                        response.setStatus(401);
                        response.setCharacterEncoding("utf-8");
                        response.setContentType("text/html; charset=UTF-8");
                        response.getWriter().write("인증되지 않은 사용자입니다.");
                    }
                });


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 모든 도메인으로부터의 요청을 허용함
        configuration.setAllowedOrigins(Arrays.asList("*")); // 모든 도메인 허용

        // 모든 헤더를 허용
        configuration.setAllowedMethods(Arrays.asList("*")); // 모든 HTTP 메서드 허용

        // 모든 종류의 HTTP 메서드 허용
        configuration.setAllowedHeaders(Arrays.asList("*"));

        //요청에 사용자 인증 정보를 포함할 수 있음 예를 들어 jwt나 세션기반 인증
        //configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 구성 적용
        return source;
    }
}


// CORS 설정



// TODO 나중에 해야 할것 : CORS 공부 심화적으로 하기, Spring Security 다시한번 제대로 공부하기


/**
 * 사용자가 웹 애플리케이션의 "OAuth2 제공자와 로그인" 버튼을 클릭합니다.

 애플리케이션은 사용자를 OAuth2 제공자의 로그인 페이지로 리디렉션합니다.

 사용자는 OAuth2 제공자의 로그인 페이지에서 자격 증명을 입력하고 제출합니다.

 OAuth2 제공자는 사용자의 자격 증명을 확인하고, 애플리케이션이 특정 사용자 정보(예: 이름, 이메일)에 접근하는 것에 대한 권한을 부여하도록 사용자에게 요청합니다.

 사용자가 권한을 부여하면, OAuth2 제공자는 인증 코드를 생성하고 애플리케이션의 콜백 URL로 사용자를 리디렉션합니다.

 애플리케이션은 콜백 URL에서 인증 코드를 받고, 해당 코드를 OAuth2 제공자와 교환하여 액세스 토큰을 받습니다.

 애플리케이션은 액세스 토큰을 사용하여 OAuth2 제공자의 사용자 정보 엔드포인트에 사용자 정보를 요청합니다.

 구성에서 지정한 커스텀 사용자 서비스(customOAuth2UserService)가 OAuth2 제공자로부터 받은 사용자 정보를 처리합니다. 여기서는 사용자 세부 정보를 애플리케이션의 사용자 모델에 매핑하거나 사용자 정의 로직을 수행할 수 있습니다.

 모든 과정이 원활하게 진행된다면, 사용자는 로그인된 것으로 간주되며 성공 처리기(oAuth2LoginSuccessHandler)가 호출됩니다. 성공 처리기는 사용자를 대시보드나 특정 페이지로 리디렉션하는 등의 추가적인 작업을 수행할 수 있습니다.

 만약 과정 중에 오류가 발생한다면, 실패 처리기(oAuth2LoginFailureHandler)가 호출되어 오류를 처리하고 사용자에게 적절한 피드백을 제공합니다.*/
