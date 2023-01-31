package nextstep.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nextstep.domain.persist.Member;

@Getter
@AllArgsConstructor
public class MemberRequest {
    private String username;
    private String password;
    private String name;
    private String phone;
    private String role;

    public Member toEntity() {
        return new Member(username, password, name, phone, role);
    }
}
