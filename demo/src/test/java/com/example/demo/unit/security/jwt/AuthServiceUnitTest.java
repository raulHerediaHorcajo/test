package com.example.demo.unit.security.jwt;

import com.example.demo.security.jwt.AuthService;
import com.example.demo.security.jwt.component.JwtCookieManager;
import com.example.demo.security.jwt.component.JwtTokenProvider;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import com.example.demo.security.jwt.dto.Token;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceUnitTest {

    private AuthService authService;

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private JwtCookieManager cookieUtil;

    @BeforeEach
    public void setUp() {
        authService = new AuthService(
            authenticationManager,
            userDetailsService,
            jwtTokenProvider,
            cookieUtil);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("loginScenarios")
    void testLogin(String scenario, boolean validateAccessToken, boolean validateRefreshToken,
        List<String> expectedCookies, List<Integer> expectedTimes
    ) {
        LoginRequest loginRequest = new LoginRequest("username", "password");
        String encryptedAccessToken = "encryptedAccessToken";
        String encryptedRefreshToken = "encryptedRefreshToken";
        when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword())
        )).thenReturn(mock(Authentication.class));
        UserDetails user = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(loginRequest.getUsername())).thenReturn(user);
        when(jwtTokenProvider.validateToken(any())).thenReturn(validateAccessToken, validateRefreshToken);
        Token newAccessToken = mock(Token.class);
        Token newRefreshToken = mock(Token.class);
        when(jwtTokenProvider.generateToken(user)).thenReturn(newAccessToken);
        lenient().when(jwtTokenProvider.generateRefreshToken(user)).thenReturn(newRefreshToken);
        HttpCookie httpCookieAccessToken = mock(HttpCookie.class);
        when(httpCookieAccessToken.toString()).thenReturn("AuthToken=eyXXXXXXXXXXXXXX");
        HttpCookie httpCookieRefreshToken = mock(HttpCookie.class);
        lenient().when(httpCookieRefreshToken.toString()).thenReturn("RefreshToken=eyYYYYYYYYYYYYYY");
        when(cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue())).thenReturn(httpCookieAccessToken);
        lenient().when(cookieUtil.createRefreshTokenCookie(newRefreshToken.getTokenValue())).thenReturn(httpCookieRefreshToken);

        ResponseEntity<AuthResponse> result = authService.login(loginRequest, encryptedAccessToken, encryptedRefreshToken);

        verify(authenticationManager).authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword())
        );
        verify(userDetailsService).loadUserByUsername(loginRequest.getUsername());
        verify(jwtTokenProvider, times(2)).validateToken(any());
        verify(jwtTokenProvider).generateToken(user);
        verify(jwtTokenProvider, times(expectedTimes.get(0))).generateRefreshToken(user);
        verify(cookieUtil).createAccessTokenCookie(newAccessToken.getTokenValue());
        verify(cookieUtil, times(expectedTimes.get(1))).createRefreshTokenCookie(newRefreshToken.getTokenValue());
        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .isNotNull()
            .containsAll(expectedCookies);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("Auth successful. Tokens are created in cookie.");
    }

    private static Stream<Arguments> loginScenarios() {
        return Stream.of(
            arguments("Login with Invalid Tokens",
                false,
                false,
                List.of("AuthToken=eyXXXXXXXXXXXXXX", "RefreshToken=eyYYYYYYYYYYYYYY"),
                List.of(1, 1)
            ),
            arguments("Login with Valid Access Token and Invalid Refresh Token",
                true,
                false,
                List.of("AuthToken=eyXXXXXXXXXXXXXX", "RefreshToken=eyYYYYYYYYYYYYYY"),
                List.of(1, 1)
            ),
            arguments("Login with Invalid Access Token and Valid Refresh Token",
                false,
                true,
                List.of("AuthToken=eyXXXXXXXXXXXXXX"),
                List.of(0, 0)
            ),
            arguments("Login with Valid Tokens",
                true,
                true,
                List.of("AuthToken=eyXXXXXXXXXXXXXX", "RefreshToken=eyYYYYYYYYYYYYYY"),
                List.of(1, 1)
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("refreshScenarios")
    void testRefresh(String scenario, boolean validateRefreshToken, int expectedTimes,
                     List<String> expectedCookies, AuthResponse refreshResponseExpected
    ) {
        String encryptedRefreshToken = "encryptedRefreshToken";
        when(jwtTokenProvider.validateToken(any())).thenReturn(validateRefreshToken);
        String username = "username";
        lenient().when(jwtTokenProvider.getUsername(any())).thenReturn(username);
        UserDetails user = mock(UserDetails.class);
        lenient().when(userDetailsService.loadUserByUsername(username)).thenReturn(user);
        Token newAccessToken = mock(Token.class);
        lenient().when(jwtTokenProvider.generateToken(user)).thenReturn(newAccessToken);
        HttpCookie httpCookieAccessToken = mock(HttpCookie.class);
        lenient().when(httpCookieAccessToken.toString()).thenReturn("AuthToken=eyXXXXXXXXXXXXXX");
        lenient().when(cookieUtil.createAccessTokenCookie(newAccessToken.getTokenValue())).thenReturn(httpCookieAccessToken);

        ResponseEntity<AuthResponse> result = authService.refresh(encryptedRefreshToken);

        verify(jwtTokenProvider).validateToken(any());
        verify(jwtTokenProvider, times(expectedTimes)).getUsername(any());
        verify(userDetailsService, times(expectedTimes)).loadUserByUsername(username);
        verify(jwtTokenProvider, times(expectedTimes)).generateToken(user);
        verify(cookieUtil, times(expectedTimes)).createAccessTokenCookie(newAccessToken.getTokenValue());
        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .matches(cookie -> validateRefreshToken ?
                cookie != null && expectedCookies.containsAll(cookie)
                :cookie == null);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(refreshResponseExpected.getStatus());
        assertThat(result.getBody().getMessage()).isEqualTo(refreshResponseExpected.getMessage());
    }

    private static Stream<Arguments> refreshScenarios() {
        return Stream.of(
            arguments("Refresh with Invalid Refresh Token",
                false,
                0,
                List.of(),
                new AuthResponse(
                    AuthResponse.Status.FAILURE,
                    "Invalid refresh token!"
                )
            ),
            arguments("Refresh with Valid Refresh Token",
                true,
                1,
                List.of("AuthToken=eyXXXXXXXXXXXXXX"),
                new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Auth successful. Token is created in cookie."
                )
            )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("logoutScenarios")
    void testLogout(String scenario, boolean hasSession, boolean hasCookies,
                    List<Integer> expectedTimes, List<String> expectedCookies
    ) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(request.getSession(false)).thenReturn(
            hasSession ?
                session
                :null);
        Cookie cookie = mock(Cookie.class);
        when(request.getCookies()).thenReturn(
            hasCookies ?
                new Cookie[]{cookie}
                : null);

        HttpCookie httpCookieToken = mock(HttpCookie.class);
        lenient().when(httpCookieToken.toString()).thenReturn("cookie=");
        lenient().when(cookieUtil.deleteTokenCookie(cookie)).thenReturn(httpCookieToken);

        ResponseEntity<AuthResponse> result = authService.logout(request);

        verify(request).getSession(false);
        verify(session, times(expectedTimes.get(0))).invalidate();
        verify(request, times(expectedTimes.get(1))).getCookies();
        verify(cookieUtil, times(expectedTimes.get(2))).deleteTokenCookie(cookie);
        assertThat(result).isNotNull();
        assertThat(result.getHeaders()).isNotNull();
        assertThat(result.getHeaders().get(HttpHeaders.SET_COOKIE))
            .matches(c -> hasCookies ?
                c != null && expectedCookies.containsAll(c)
                :c == null);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getStatus()).isEqualTo(AuthResponse.Status.SUCCESS);
        assertThat(result.getBody().getMessage()).isEqualTo("logout successfully");
    }

    private static Stream<Arguments> logoutScenarios() {
        return Stream.of(
            arguments("Logout with Sessions and without Cookies",
                true,
                false,
                List.of(1, 1, 0),
                List.of()
            ),
            arguments("Logout without Sessions and with Cookies",
                false,
                true,
                List.of(0, 2, 1),
                List.of("cookie=")
            )
        );
    }
}