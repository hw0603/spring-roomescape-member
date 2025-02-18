package roomescape.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import roomescape.exception.NotEnoughPermissionException;
import roomescape.service.member.MemberService;
import roomescape.service.member.dto.MemberResponse;

@Component
public class CheckAdminInterceptor implements HandlerInterceptor {
    private final MemberTokenConverter memberTokenConverter;
    private final MemberService memberService;

    public CheckAdminInterceptor(MemberTokenConverter memberTokenConverter, MemberService memberService) {
        this.memberTokenConverter = memberTokenConverter;
        this.memberService = memberService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        MemberResponse member = memberTokenConverter.getMemberResponseFromCookies(request.getCookies());
        if (memberService.checkAdmin(member.id())) {
            return true;
        }
        throw new NotEnoughPermissionException("해당 페이지에 접근할 수 있는 계정으로 로그인하지 않았습니다.");
    }
}
