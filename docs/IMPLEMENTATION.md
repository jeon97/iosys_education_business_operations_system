# 구현 상세

## 신청 정원 검증과 일괄 저장

신청 처리 전 기관별 허용 인원, 시험실 수용 인원, 현재 신청 인원을 확인했습니다. 신청 가능 여부가 확인된 뒤 신청정보, 수험번호, 설문과 답변을 하나의 저장 단위로 처리했습니다.

```text
기존 신청 확인
  → 기관 정원 확인
  → 시험실 수용 인원 확인
  → 현재 신청 인원 확인
  → 신청과 수험번호 생성
  → 설문 및 답변 저장
```

재작성 코드: [EnrollmentService](../samples/business-operations/src/main/java/com/portfolio/education/enrollment/EnrollmentService.java)

## 예산 변경 상태와 이력

예산 변경은 세부항목 합계가 전체 변경 금액과 맞아야 하며, 제출·승인·반려 상태에 따라 가능한 작업이 다릅니다. 상태를 바꿀 때마다 이전 상태와 사유를 이력으로 남기도록 구성했습니다.

재작성 코드: [BudgetChangeService](../samples/business-operations/src/main/java/com/portfolio/education/budget/BudgetChangeService.java)

## 대량 메일 발송 결과 관리

한 사용자의 데이터가 잘못됐다고 전체 발송을 중단하지 않고 수신자별로 검증과 발송 결과를 수집했습니다. 성공한 대상만 발송일시를 기록하고 실패 대상은 재처리할 수 있도록 이유와 함께 반환했습니다.

재작성 코드: [BulkNotificationService](../samples/business-operations/src/main/java/com/portfolio/education/notification/BulkNotificationService.java)

## 공개 코드 작성 기준

- 원본 클래스명, 메서드명, Mapper 쿼리와 화면 코드를 복사하지 않았습니다.
- 사업명, 기관 코드, 사용자 개인정보, 메일 주소와 운영 설정을 제거했습니다.
- 실제 담당 기능의 처리 구조만 Java 17 코드로 다시 작성했습니다.
- 정상 처리, 정원 초과, 잘못된 상태, 부분 발송 실패를 단위 테스트로 검증합니다.

