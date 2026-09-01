# 기능별 구현 근거

| 담당 영역 | 개발한 기능 | 구현 방식 | 공개 예제 |
|---|---|---|---|
| 신청 | 정원 검증, 신청·수험번호·설문 저장 | 기관·시험실 수용 인원 확인 후 하나의 저장 단위로 처리 | [EnrollmentService](../samples/business-operations/src/main/java/com/portfolio/education/enrollment/EnrollmentService.java) |
| 예산 변경 | 세부금액 변경, 제출·승인·반려, 이력 | 합계 검증과 명시적인 상태 전이 | [BudgetChangeService](../samples/business-operations/src/main/java/com/portfolio/education/budget/BudgetChangeService.java) |
| 정산자료 | 자료 전송, 승인·반려, 반려 사유와 환율 갱신 | 제출 상태에서만 승인·반려하고 모든 변경 이력 보존 | [SettlementService](../samples/business-operations/src/main/java/com/portfolio/education/settlement/SettlementService.java) |
| 대량 안내 | 워크숍·신청 안내 메일 | 대상자별 검증 후 성공과 실패를 분리해 반환 | [BulkNotificationService](../samples/business-operations/src/main/java/com/portfolio/education/notification/BulkNotificationService.java) |
| 보조금·배송 | 반환, 계좌, 주소, 수량 상태 관리 | 처리 상태와 관리자 확인 결과를 분리 | [기여 내역](CONTRIBUTIONS.md) |
| 보고서 | 성적·신청 결과, 출력 이력 | 보고서 데이터 조회와 출력 로그 저장 | [구현 상세](IMPLEMENTATION.md) |

