package roomescape.infrastructure;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import roomescape.Fixtures;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@Import(ReservationDao.class)
@DisplayName("예약 DAO")
class ReservationDaoTest {

    private final ReservationRepository reservationRepository;

    @Autowired
    public ReservationDaoTest(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    @DisplayName("예약 DAO는 생성 요청이 들어오면 DB에 값을 저장한다.")
    @Test
    void save() {
        Reservation reservation = new Reservation(
                Fixtures.memberFixture,
                LocalDate.of(2024, 11, 16),
                Fixtures.reservationTimeFixture,
                Fixtures.themeFixture
        );

        // when
        Reservation newReservation = reservationRepository.save(reservation);
        Optional<Reservation> actual = reservationRepository.findById(newReservation.getId());

        // then
        assertThat(actual).isPresent();
    }

    @DisplayName("예약 DAO는 조회 요청이 들어오면 id에 맞는 값을 반환한다.")
    @Test
    void findById() {
        // when
        Optional<Reservation> actual = reservationRepository.findById(1L);

        // then
        assertThat(actual).isPresent();
    }

    @DisplayName("예약 DAO는 조회 요청이 들어오면 저장한 모든 값을 반환한다.")
    @Test
    void findAll() {
        // when
        List<Reservation> reservations = reservationRepository.findAll();

        // then
        assertThat(reservations).hasSize(7);
    }

    @DisplayName("예약 DAO는 주어진 기간 동안의 모든 예약을 반환한다.")
    @Test
    void findByDateBetween() {
        // given
        LocalDate startDate = LocalDate.of(2024, 12, 1);
        LocalDate endDate = LocalDate.of(2024, 12, 8);

        // when
        List<Reservation> reservations = reservationRepository.findByDateBetween(startDate, endDate);

        // then
        assertThat(reservations).hasSize(5);
    }

    @DisplayName("예약 DAO는 주어진 날짜와 테마에 맞는 예약을 반환한다.")
    @Test
    void findByDateAndThemeId() {
        // given
        LocalDate date = LocalDate.of(2024, 12, 2);
        Long themeId = 2L;

        // when
        List<Reservation> reservations = reservationRepository.findByDateAndThemeId(date, themeId);

        // then
        assertThat(reservations).hasSize(2);
    }

    @DisplayName("예약 DAO는 삭제 요청이 들어오면 id에 맞는 값을 삭제한다.")
    @Test
    void deleteById() {
        // given
        Long id = 1L;

        // when
        reservationRepository.deleteById(id);
        Optional<Reservation> actual = reservationRepository.findById(id);

        // then
        assertThat(actual).isNotPresent();
    }
}
