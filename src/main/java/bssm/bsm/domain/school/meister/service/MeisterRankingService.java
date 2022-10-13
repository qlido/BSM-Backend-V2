package bssm.bsm.domain.school.meister.service;

import bssm.bsm.domain.school.meister.facade.MeisterInfoFacade;
import bssm.bsm.domain.school.meister.presentation.dto.response.MeisterRankingResponse;
import bssm.bsm.domain.school.meister.presentation.dto.response.MeisterStudentResponse;
import bssm.bsm.domain.school.meister.entities.MeisterInfo;
import bssm.bsm.domain.school.meister.repositories.MeisterDataRepository;
import bssm.bsm.domain.school.meister.repositories.MeisterInfoRepository;
import bssm.bsm.domain.school.meister.entities.MeisterInfoResultType;
import bssm.bsm.domain.user.entities.Student;
import bssm.bsm.domain.user.entities.User;
import bssm.bsm.global.error.exceptions.ForbiddenException;
import bssm.bsm.global.error.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MeisterRankingService {

    private final MeisterInfoRepository meisterInfoRepository;
    private final MeisterDataRepository meisterDataRepository;
    private final MeisterInfoFacade meisterInfoFacade;

    public void updatePrivateRanking(User user, boolean privateRanking) {
        MeisterInfo meisterInfo = meisterInfoRepository.findById(user.getStudentId()).orElseThrow(
                () -> {throw new NotFoundException("마이스터 정보를 가져올 수 없습니다");}
        );

        LocalDateTime availableTime = meisterInfo.getLastPrivateDate().plusDays(1);
        if (LocalDateTime.now().isBefore(availableTime)) {
            long diffSecond = Duration.between(LocalDateTime.now(), availableTime).getSeconds();
            throw new ForbiddenException(String.valueOf(diffSecond));
        }

        meisterInfo.setLastPrivateDate(LocalDateTime.now());
        meisterInfo.setPrivateRanking(privateRanking);
        meisterInfoRepository.save(meisterInfo);
    }

    public List<MeisterRankingResponse> getRanking(User user) {
        meisterInfoFacade.getMeisterInfo(user.getStudentId()).permissionCheck();

        return meisterDataRepository.findByOrderByScoreDesc().stream()
                .map(meisterData -> {
                    Student student = meisterData.getMeisterInfo().getStudent();
                    MeisterRankingResponse.MeisterRankingResponseBuilder builder = MeisterRankingResponse.builder()
                            .student(MeisterStudentResponse.builder()
                                    .grade(student.getGrade())
                                    .classNo(student.getClassNo())
                                    .studentNo(student.getStudentNo())
                                    .name(student.getName())
                                    .build()
                            )
                            .result(convertResult(meisterData.getMeisterInfo()));

                    if (meisterData.getMeisterInfo().isPrivateRanking()) {
                        return builder.build();
                    }

                    return builder
                            .score(meisterData.getScore())
                            .positivePoint(meisterData.getPositivePoint())
                            .negativePoint(meisterData.getNegativePoint())
                            .lastUpdate(meisterData.getModifiedAt())
                            .build();
                    }
                ).sorted(MeisterRankingResponse::compareTo)
                .collect(Collectors.toList());
    }

    private MeisterInfoResultType convertResult(MeisterInfo meisterInfo) {
        if (meisterInfo.isPrivateRanking()) return MeisterInfoResultType.PRIVATE;
        if (meisterInfo.isLoginError()) return MeisterInfoResultType.LOGIN_ERROR;
        return MeisterInfoResultType.SUCCESS;
    }

}
