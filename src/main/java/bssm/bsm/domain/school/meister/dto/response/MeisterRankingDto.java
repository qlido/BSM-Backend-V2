package bssm.bsm.domain.school.meister.dto.response;

import bssm.bsm.domain.school.meister.type.MeisterInfoResultType;
import bssm.bsm.domain.user.entities.Student;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MeisterRankingDto {

    private Float score;
    private Integer positivePoint;
    private Integer negativePoint;
    private LocalDateTime lastUpdate;
    private MeisterStudentResponseDto student;
    private MeisterInfoResultType result;
}
