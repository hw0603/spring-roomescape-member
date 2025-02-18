package roomescape.service.reservation;

import java.time.LocalDate;
import org.springframework.stereotype.Service;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.IllegalTimeException;
import roomescape.domain.member.MemberRepository;
import roomescape.service.reservation.dto.ReservationCreateRequest;
import roomescape.service.reservation.dto.ReservationResponse;
import roomescape.exception.ResourceNotFoundException;
import roomescape.domain.theme.ThemeRepository;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservationtime.ReservationTimeRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationTimeRepository reservationTimeRepository;
    private final ThemeRepository themeRepository;
    private final MemberRepository memberRepository;

    public ReservationService(
            ReservationRepository reservationRepository,
            ReservationTimeRepository reservationTimeRepository,
            ThemeRepository themeRepository,
            MemberRepository memberRepository) {
        this.reservationRepository = reservationRepository;
        this.reservationTimeRepository = reservationTimeRepository;
        this.themeRepository = themeRepository;
        this.memberRepository = memberRepository;
    }

    public ReservationResponse createReservation(ReservationCreateRequest request) {
        Member member = findMemberById(request.memberId());
        ReservationTime reservationTime = findReservationTimeById(request.timeId());
        Theme theme = findThemeById(request.themeId());
        Reservation reservation = request.toReservation(member, reservationTime, theme);

        validateDuplicated(reservation);
        validateRequestedTime(reservation, reservationTime);

        Reservation savedReservation = reservationRepository.save(reservation);
        return ReservationResponse.from(savedReservation);
    }

    private Member findMemberById(long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 사용자입니다."));
    }

    private ReservationTime findReservationTimeById(Long id) {
        return reservationTimeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약 시간입니다."));
    }

    private Theme findThemeById(Long id) {
        return themeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 테마입니다."));
    }

    private void validateDuplicated(Reservation reservation) {
        boolean isDuplicated = reservationRepository.existsBy(reservation.getDate(), reservation.getTimeId(),
                reservation.getThemeId());
        if (isDuplicated) {
            throw new IllegalTimeException("해당 시간대에 이미 예약된 테마입니다.");
        }
    }

    private void validateRequestedTime(Reservation reservation, ReservationTime reservationTime) {
        LocalDateTime requestedDateTime = LocalDateTime.of(reservation.getDate(), reservationTime.getStartAt());
        if (requestedDateTime.isBefore(LocalDateTime.now())) {
            throw new IllegalTimeException("이미 지난 날짜는 예약할 수 없습니다.");
        }
    }

    public List<ReservationResponse> readReservations() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponse::from)
                .toList();
    }

    public ReservationResponse readReservation(Long id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("존재하지 않는 예약입니다."));
        return ReservationResponse.from(reservation);
    }

    public void deleteReservation(Long id) {
        reservationRepository.deleteById(id);
    }

    public List<ReservationResponse> findFilteredBy(
            long memberId, long themeId,
            LocalDate startDateInclusive, LocalDate endDateInclusive
    ) {
        return reservationRepository.findByMemberAndThemeAndDateBetween(
                        memberId, themeId, startDateInclusive, endDateInclusive.plusDays(1)).stream()
                .map(ReservationResponse::from)
                .toList();
    }
}
