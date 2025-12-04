"use strict";
// 2. 예약 생성 API 호출 함수
// 서버로 HTTP POST 요청을 보냄
// @Param data - 예약 폼에서 받은 데이터 (ReservationFormData 타입)
// @ returns 예약 성공/ 실패 메시지
async function createReservation(data) {
    // API 쿼리 파라미터 생성 | 예: userId=1&doctorId=1&reservationTime=...
    const params = new URLSearchParams({
        userId: data.userId.toString(),
        doctorId: data.doctorId.toString(),
        reservationTime: data.reservationTime
    });
    // API 엔드포인트 URL 조합
    const url = `/api/v1/reservations?${params.toString()}`;
    try { // fetch 함수로 HTTP POST 요청
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });
        if (response.ok) { // 성공
            const reservationId = await response.text();
            return `예약 성공. 예약 번호: ${reservationId}`;
        }
        else if (response.status === 400) {
            return `이미 예약된 시간입니다.`;
        }
        else {
            return `서버 오류: ${response.status} ${response.statusText}`;
        }
    }
    catch (error) {
        return `네트워크 오류 발생`;
    }
}
// 3. 웹 페이지(DOM) 이벤트 처리
//HTML 문서가 완전히 로드되면 실행되어 폼 제출 이벤트를 감지하고 API 호출
document.addEventListener('DOMContentLoaded', () => {
    // 폼과 결과를 표시할 영역ㅇㄹ HTML ID로 찾음
    const form = document.getElementById('reservationForm');
    const resultDiv = document.getElementById('result');
    if (form) {
        form.addEventListener('submit', async (event) => {
            event.preventDefault(); // 새로고침 방지
            // 입력창에서 값을 가져옴
            const userIdInput = document.getElementById('userId');
            const doctorIdInput = document.getElementById('doctorId');
            const timeInput = document.getElementById('reservationTime');
            // 값이 다 있는지 확인
            if (!userIdInput.value || !doctorIdInput.value || !timeInput.value) {
                resultDiv.innerText = "모든 값을 입력해주세요.";
                return;
            }
            const data = {
                userId: parseInt(userIdInput.value),
                doctorId: parseInt(doctorIdInput.value),
                reservationTime: timeInput.value
            };
            resultDiv.innerText = '예약 처리 중';
            // 결과를 기다렸다가 화면에 표시
            const result = await createReservation(data);
            resultDiv.innerText = result;
        });
    }
});
