package nextstep.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import nextstep.domain.persist.Member;

@Getter
@AllArgsConstructor
@Builder
public class MemberRequest {
    @Schema(description = "유저 아이디")
    private String username;
    @Schema(description = "유저 패스워드")
    private String password;
    @Schema(description = "유저 이름")
    private String name;
    @Schema(description = "유저 전화번호")
    private String phone;
    @Schema(description = "유저 권한")
    private String role;

    public Member toEntity() {
        return new Member(username, password, name, phone, role);
    }
}
