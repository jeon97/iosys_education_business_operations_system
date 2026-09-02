# 성적증명서 발급과 확인

신청자 화면에서 시험 결과를 조회하고 성적증명서를 출력하며, 운영자는 발급 이력을 확인할 수 있도록 구성했습니다. 공개 확인 화면에서는 개인정보 대신 검증용 코드로 증명서 상태를 조회하도록 분리했습니다.

## 처리 기준

- 확정된 결과만 발급
- 같은 요청 ID의 재전송은 기존 발급 결과 반환
- 외부 확인에는 예측하기 어려운 공개 검증 코드 사용
- 취소된 증명서는 다시 유효한 것으로 표시하지 않음
- 발급·취소 시각과 처리자를 이력으로 보존

[CertificateIssueService](../samples/business-operations/src/main/java/com/portfolio/education/certificate/CertificateIssueService.java)는 발급, 중복 요청, 취소와 공개 검증을 독립적인 예제로 정리했습니다.
