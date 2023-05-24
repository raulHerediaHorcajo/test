package com.example.demo.unit.security.jwt;

import com.example.demo.security.jwt.AuthService;
import com.example.demo.security.jwt.component.JwtCookieManager;
import com.example.demo.security.jwt.component.JwtTokenProvider;
import com.example.demo.security.jwt.dto.AuthResponse;
import com.example.demo.security.jwt.dto.LoginRequest;
import com.example.demo.security.jwt.dto.Token;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
        when(httpCookieAccessToken.toString()).thenReturn("eyXXXXXXXXXXXXXX");
        HttpCookie httpCookieRefreshToken = mock(HttpCookie.class);
        lenient().when(httpCookieRefreshToken.toString()).thenReturn("eyYYYYYYYYYYYYYY");
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
                List.of("eyXXXXXXXXXXXXXX", "eyYYYYYYYYYYYYYY"),
                List.of(1, 1)
            ),
            arguments("Login with Valid Access Token and Invalid Refresh Token",
                true,
                false,
                List.of("eyXXXXXXXXXXXXXX", "eyYYYYYYYYYYYYYY"),
                List.of(1, 1)
            ),
            arguments("Login with Invalid Access Token and Valid Refresh Token",
                false,
                true,
                List.of("eyXXXXXXXXXXXXXX"),
                List.of(0, 0)
            ),
            arguments("Login with Valid Tokens",
                true,
                true,
                List.of("eyXXXXXXXXXXXXXX", "eyYYYYYYYYYYYYYY"),
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
        lenient().when(httpCookieAccessToken.toString()).thenReturn("eyXXXXXXXXXXXXXX");
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
                List.of("eyXXXXXXXXXXXXXX"),
                new AuthResponse(
                    AuthResponse.Status.SUCCESS,
                    "Auth successful. Token is created in cookie."
                )
            )
        );
    }

    /*@Test
    void testFindAll() {
        SocietyCriteria filters = mock(SocietyCriteria.class);
        Specification<Society> specification = new SocietySpecification(filters);
        Pageable pageable = PageRequest.of(0, 20);
        List<Society> societies = List.of(
            new Society("XXXXXXXXXX","Test Society 1"),
            new Society("YYYYYYYYYY","Test Society 2"),
            new Society("ZZZZZZZZZZ","Test Society 3")
        );
        Page<Society> page = new PageImpl<>(societies, pageable, 3);

        when(societyRepository.findAll(specification, pageable)).thenReturn(page);

        Page<Society> result = authService.findAll(filters, pageable);

        verify(societyRepository).findAll(specification, pageable);
        assertThat(result).isNotNull();
        assertThat(result.getNumberOfElements()).isEqualTo(3);
        assertThat(result.getContent()).containsAll(societies);
        assertThat(result.getPageable()).isEqualTo(pageable);
    }

    @Test
    void whenFindByIdSocietyDoesNotExist_thenShouldGiveOptionalEmpty() {
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        Optional<Society> resultSociety = authService.findById(1);

        verify(societyRepository).findById((long) 1);
        assertThat(resultSociety)
            .isNotPresent();
    }

    @Test
    void testFindById() {
        Society expectedSociety = new Society(1, "cifDni", "name");

        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(expectedSociety));

        Optional<Society> resultSociety = authService.findById(1);

        verify(societyRepository).findById((long) 1);
        assertThat(resultSociety)
            .isPresent()
            .contains((expectedSociety));
    }

    @Test
    void testAddSociety(){
        Society society = new Society("cifDni", "name");
        Society expectedSociety = new Society(1, "cifDni", "name");

        when(societyRepository.save(society)).thenReturn(expectedSociety);

        Society resultSociety = authService.addSociety(society);

        verify(societyRepository).save(society);
        assertThat(resultSociety).isEqualTo(expectedSociety);
    }

    @Test
    void whenUpdateSocietyDoesNotExist_thenShouldGiveSocietyNotFoundException() {
        Society newSociety = new Society("newCifDni", "newName");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.updateSociety(1, newSociety))
            .isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");

        verify(societyRepository).findById((long) 1);
        verify(societyRepository, never()).save(any(Society.class));
    }

    @Test
    void testUpdateSociety() {
        Society newSociety = new Society("newCifDni", "newName");
        Society storedSociety = new Society(1, "cifDni", "name");
        Society expectedSociety = new Society(1, "newCifDni", "newName");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(storedSociety));
        when(societyRepository.save(newSociety)).thenReturn(expectedSociety);

        Society resultSociety = authService.updateSociety(1, newSociety);

        verify(societyRepository).findById((long) 1);
        verify(societyRepository).save(newSociety);
        assertThat(resultSociety).isEqualTo(expectedSociety);
    }

    @Test
    void whenDeleteSocietyDoesNotExist_thenShouldGiveSocietyNotFoundException() {
        when(societyRepository.findById((long) 1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.deleteSociety(1))
            .isInstanceOf(SocietyNotFoundException.class)
            .hasMessageContaining("Society 1 not found");

        verify(societyRepository).findById((long) 1);
        verify(societyRepository, never()).delete(any(Society.class));
    }

    @Test
    void testDeleteSociety() {
        Society society = new Society(1, "cifDni", "name");
        when(societyRepository.findById((long) 1)).thenReturn(Optional.of(society));

        authService.deleteSociety(1);

        verify(societyRepository).findById((long) 1);
        verify(societyRepository).delete(society);
    }*/
}