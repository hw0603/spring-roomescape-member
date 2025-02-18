package roomescape.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import roomescape.Fixtures;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("테마 DAO")
@JdbcTest
class ThemeDaoTest {

    private final ThemeRepository themeRepository;

    @Autowired
    public ThemeDaoTest(JdbcTemplate jdbcTemplate) {
        this.themeRepository = new ThemeDao(jdbcTemplate);
    }

    @DisplayName("테마 DAO는 생성 요청이 들어오면 DB에 값을 저장한다.")
    @Test
    void save() {
        // given
        Theme theme = Fixtures.themeFixture;

        // when
        Theme savedTheme = themeRepository.save(theme);
        Optional<Theme> actual = themeRepository.findById(savedTheme.getId());

        // then
        assertThat(actual).isPresent();
    }

    @DisplayName("테마 DAO는 조회 요청이 들어오면 DB에 저장된 모든 테마를 반환한다.")
    @Test
    void findAll() {
        // when
        List<Theme> themes = themeRepository.findAll();

        // then
        assertThat(themes).hasSize(3);
    }

    @DisplayName("테마 DAO는 삭제 요청이 들어오면 id에 맞는 값을 삭제한다.")
    @Test
    void delete() {
        // given
        Long id = 3L;

        // when
        themeRepository.deleteById(id);
        Optional<Theme> actual = themeRepository.findById(id);

        // then
        assertThat(actual).isNotPresent();
    }
}
