package roomescape.service;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.Fixtures;
import roomescape.exception.DuplicatedException;
import roomescape.exception.ReferencedReservationExistException;
import roomescape.service.theme.ThemeService;
import roomescape.service.theme.dto.ThemeCreateRequest;
import roomescape.service.theme.dto.ThemeResponse;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.reservation.ReservationRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static roomescape.Fixtures.themeFixture;

@ExtendWith(MockitoExtension.class)
@DisplayName("테마 서비스")
class ThemeServiceTest {
    @InjectMocks
    private ThemeService themeService;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @DisplayName("테마 서비스는 테마를 생성한다.")
    @Test
    void createTheme() {
        // given
        Mockito.when(themeRepository.save(any()))
                .thenReturn(themeFixture);
        ThemeCreateRequest request = new ThemeCreateRequest(
                "힐링",
                "완전 힐링되는 테마",
                "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg"
        );

        // when
        ThemeResponse actual = themeService.createTheme(request);

        // then
        SoftAssertions softAssertions = new SoftAssertions();
        softAssertions.assertThat(actual.name()).isEqualTo(themeFixture.getName());
        softAssertions.assertThat(actual.description()).isEqualTo(themeFixture.getDescription());
        softAssertions.assertThat(actual.thumbnail()).isEqualTo(themeFixture.getThumbnail());
        softAssertions.assertAll();
    }

    @DisplayName("테마 서비스는 테마 생성 시 중복된 이름이 들어올 경우 예외가 발생한다.")
    @Test
    void validateDuplicated() {
        // given
        Mockito.when(themeRepository.findAll())
                .thenReturn(List.of(themeFixture));
        ThemeCreateRequest request = new ThemeCreateRequest(
                "공포", "공포스러운 테마", "http://example.org"
        );

        // when & then
        assertThatThrownBy(() -> themeService.createTheme(request))
                .isInstanceOf(DuplicatedException.class)
                .hasMessage("중복된 테마 이름입니다.");
    }

    @DisplayName("테마 서비스는 모든 테마를 조회한다.")
    @Test
    void findAll() {
        // given
        Mockito.when(themeRepository.findAll())
                .thenReturn(List.of(themeFixture));

        // when
        List<ThemeResponse> themeResponses = themeService.readThemes();

        // then
        assertThat(themeResponses).hasSize(1);
    }

    @DisplayName("테마 서비스는 최근 일주일 간의 인기 있는 테마를 조회힌다.")
    @Test
    void readPopularThemes() {
        // given
        List<Long> expected = List.of(5L, 1L, 2L, 3L, 4L);
        Mockito.when(reservationRepository.findByDateBetween(any(), any()))
                .thenReturn(Fixtures.reservationFixturesForPopularTheme);

        // when
        List<ThemeResponse> popularThemes = themeService.readPopularThemes();
        List<Long> actual = popularThemes.stream()
                .mapToLong(ThemeResponse::id)
                .boxed()
                .toList();

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("테마 서비스는 id에 해당하는 테마를 삭제한다.")
    @Test
    void delete() {
        // given
        Long id = 1L;
        Mockito.when(reservationRepository.existsByThemeId(id))
                .thenReturn(false);
        Mockito.doNothing().when(themeRepository).deleteById(id);

        // when & then
        assertThatCode(() -> themeService.deleteTheme(id))
                .doesNotThrowAnyException();
    }

    @DisplayName("테마 서비스는 id에 해당하는 테마 삭제 시 예약이 있는 경우 예외가 발생한다.")
    @Test
    void deleteThemeWithExistsReservation() {
        // given
        Long id = 1L;
        Mockito.when(reservationRepository.existsByThemeId(id))
                .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> themeService.deleteTheme(id))
                .isInstanceOf(ReferencedReservationExistException.class)
                .hasMessage("해당 테마에 예약이 존재합니다.");
    }
}
