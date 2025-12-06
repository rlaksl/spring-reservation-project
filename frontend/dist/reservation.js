"use strict";
// ===== 2. 전역 상태 관리 (선택된 값들을 저장)
let selectedDepartmentId = null;
let selectedDoctorId = null;
let selectedDate = null;
let selectedTime = null;
// ===== 3. API 호출 함수들
// 진료과 목록 가져오기
async function fetchDepartments() {
    try {
        const response = await fetch('/api/v1/departments');
        if (!response.ok) {
            throw new Error('진료과 목록을 가져오는데 실패했습니다.');
        }
        const departments = await response.json();
        return departments;
    }
    catch (error) {
        console.error('진료과 조회 오류:', error);
        return [];
    }
}
// 특정 진료과의 의사 목록 가져오기
async function fetchDoctors(departmentId) {
    try {
        const response = await fetch(`/api/v1/departments/${departmentId}/doctors`);
        if (!response.ok) {
            throw new Error('의사 목록을 가져오는데 실패했습니다.');
        }
        const doctors = await response.json();
        return doctors;
    }
    catch (error) {
        console.error('의사 조회 오류:', error);
        return [];
    }
}
// 특정 의사의 예약 가능 시간 가져오기
async function fetchAvailableSlots(doctorId, date) {
    try {
        const response = await fetch(`/api/v1/doctors/${doctorId}/availability?date=${date}`);
        if (!response.ok) {
            throw new Error('예약 가능 시간을 가져오는데 실패했습니다.');
        }
        const slots = await response.json();
        return slots;
    }
    catch (error) {
        console.error('예약 가능 시간 조회 오류:', error);
        return [];
    }
}
// 예약 생성하기
async function createReservation(data) {
    try {
        const params = new URLSearchParams({
            userId: data.userId.toString(),
            doctorId: data.doctorId.toString(),
            reservationTime: data.reservationTime
        });
        const response = await fetch(`/api/v1/reservations?${params.toString()}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
        });
        if (response.ok) {
            const reservationId = await response.text();
            return `예약 성공! 예약 번호: ${reservationId}`;
        }
        else if (response.status === 400) {
            return '이미 예약된 시간입니다.';
        }
        else {
            return `서버 오류: ${response.status}`;
        }
    }
    catch (error) {
        console.error('예약 생성 오류:', error);
        return '네트워크 오류가 발생했습니다.';
    }
}
// ===== 4. UI 업데이트 함수들
// 진료과 드롭다운 채우기
function populateDepartments(departments) {
    const select = document.getElementById('departmentSelect');
    // 기존 옵션 제거 (첫 번째 "선택하세요" 옵션은 유지)
    while (select.options.length > 1) {
        select.remove(1);
    }
    // 새로운 옵션 추가
    departments.forEach(dept => {
        const option = document.createElement('option');
        option.value = dept.id.toString();
        option.textContent = dept.deptName;
        select.appendChild(option);
    });
}
// 의사 카드 목록 생성
function populateDoctors(doctors) {
    const doctorList = document.getElementById('doctorList');
    // 기존 카드 전체 제거
    doctorList.innerHTML = '';
    // 각 의사마다 카드 생성
    doctors.forEach(doctor => {
        const card = document.createElement('div');
        card.className = 'doctor-card';
        card.dataset.doctorId = doctor.id.toString();
        // 카드 내용
        card.innerHTML = `
      <div class="doctor-name">${doctor.name}</div>
      <div class="doctor-info">휴무: ${translateDayOff(doctor.dayOff)}</div>
    `;
        // 클릭 이벤트: 의사 선택
        card.addEventListener('click', () => selectDoctor(doctor.id));
        doctorList.appendChild(card);
    });
}
// 시간 슬롯 버튼 생성
function populateTimeSlots(slots) {
    const timeSlots = document.getElementById('timeSlots');
    // 기존 버튼 전체 제거
    timeSlots.innerHTML = '';
    if (slots.length === 0) {
        // 예약 가능 시간이 없으면 안내 메시지
        timeSlots.innerHTML = '<p style="color: #999;">예약 가능한 시간이 없습니다.</p>';
        return;
    }
    // 각 시간마다 버튼 생성
    slots.forEach(slot => {
        const button = document.createElement('div');
        button.className = 'time-slot';
        button.textContent = slot;
        button.dataset.time = slot;
        // 클릭 이벤트: 시간 선택
        button.addEventListener('click', () => selectTime(slot));
        timeSlots.appendChild(button);
    });
}
// ===== 5. 선택 처리 함수들
// 진료과 선택 처리
async function handleDepartmentChange(departmentId) {
    if (!departmentId) {
        // "선택하세요" 선택 시 초기화
        resetDoctorSection();
        return;
    }
    selectedDepartmentId = parseInt(departmentId);
    // 의사 목록 가져오기
    const doctors = await fetchDoctors(selectedDepartmentId);
    if (doctors.length > 0) {
        populateDoctors(doctors);
        showSection('doctorSection');
    }
    else {
        alert('해당 진료과에 의사가 없습니다.');
    }
}
// 의사 선택 처리
function selectDoctor(doctorId) {
    selectedDoctorId = doctorId;
    // 모든 의사 카드에서 selected 클래스 제거
    const allCards = document.querySelectorAll('.doctor-card');
    allCards.forEach(card => card.classList.remove('selected'));
    // 선택한 카드에만 selected 클래스 추가
    const selectedCard = document.querySelector(`[data-doctor-id="${doctorId}"]`);
    if (selectedCard) {
        selectedCard.classList.add('selected');
    }
    // 날짜 입력 활성화
    const dateInput = document.getElementById('dateInput');
    dateInput.disabled = false;
    dateInput.min = getTodayString();
    showSection('dateSection');
    // 시간 섹션은 숨김 (날짜 선택 전까지)
    hideSection('timeSection');
    selectedDate = null;
    selectedTime = null;
    updateSubmitButton();
}
// 날짜 선택 처리
async function handleDateChange(date) {
    if (!date || !selectedDoctorId) {
        return;
    }
    selectedDate = date;
    // 예약 가능 시간 가져오기
    const slots = await fetchAvailableSlots(selectedDoctorId, selectedDate);
    populateTimeSlots(slots);
    showSection('timeSection');
    // 시간 선택 초기화
    selectedTime = null;
    updateSubmitButton();
}
// 시간 선택 처리
function selectTime(time) {
    selectedTime = time;
    // 모든 시간 버튼에서 selected 클래스 제거
    const allSlots = document.querySelectorAll('.time-slot');
    allSlots.forEach(slot => slot.classList.remove('selected'));
    // 선택한 버튼에만 selected 클래스 추가
    const selectedSlot = document.querySelector(`[data-time="${time}"]`);
    if (selectedSlot) {
        selectedSlot.classList.add('selected');
    }
    updateSubmitButton();
}
// ===== 6. 예약하기 버튼 처리
async function handleSubmit() {
    // 모든 값이 선택되었는지 확인
    if (!selectedDoctorId || !selectedDate || !selectedTime) {
        alert('모든 항목을 선택해주세요.');
        return;
    }
    // 예약 시간 ISO 형식으로 변환
    const reservationTime = `${selectedDate}T${selectedTime}`;
    const reservationData = {
        userId: 1,
        doctorId: selectedDoctorId,
        reservationTime: reservationTime
    };
    // 예약 API 호출
    const result = await createReservation(reservationData);
    // 결과 표시
    showResult(result, result.includes('성공'));
    // 성공 시 폼 초기화
    if (result.includes('성공')) {
        // 선택된 정보 가져오기
        const deptName = getDepartmentName(selectedDepartmentId);
        const doctorName = getDoctorName(selectedDoctorId);
        const dateFormatted = formatDate(selectedDate);
        const reservationId = result.match(/\d+/)?.[0] || '?';
        // 확인 창 메시지 생성
        const message = `
      예약이 완료되었습니다.
      ━━━━━━━━━━━━━━━━━━━━━━━━━
      예약 정보
      ━━━━━━━━━━━━━━━━━━━━━━━━━

      진료과: ${deptName}
      담당의: ${doctorName}
      날짜: ${dateFormatted}
      시간: ${selectedTime}
      예약번호: ${reservationId}

      ━━━━━━━━━━━━━━━━━━━━━━━━━
    `;
        // 확인 창 표시
        alert(message);
        // 확인 버튼 누르면 폼 초기화
        resetForm();
    }
}
// ===== 7. UI 헬퍼 함수들
// 섹션 표시
function showSection(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.remove('hidden');
    }
}
// 섹션 숨김
function hideSection(sectionId) {
    const section = document.getElementById(sectionId);
    if (section) {
        section.classList.add('hidden');
    }
}
// 의사 섹션 이후 초기화
function resetDoctorSection() {
    hideSection('dateSection');
    hideSection('timeSection');
    const dateInput = document.getElementById('dateInput');
    dateInput.disabled = true;
    dateInput.value = '';
    selectedDoctorId = null;
    selectedDate = null;
    selectedTime = null;
    updateSubmitButton();
}
// 전체 폼 초기화
function resetForm() {
    // 드롭다운 초기화
    const select = document.getElementById('departmentSelect');
    select.value = '';
    // 모든 섹션 숨김
    hideSection('doctorSection');
    hideSection('dateSection');
    hideSection('timeSection');
    // 선택값 초기화
    selectedDepartmentId = null;
    selectedDoctorId = null;
    selectedDate = null;
    selectedTime = null;
    // 버튼 비활성화
    updateSubmitButton();
    // 결과 메시지 제거
    const resultDiv = document.getElementById('result');
    resultDiv.innerHTML = '';
}
// 예약하기 버튼 활성화/비활성화
function updateSubmitButton() {
    const button = document.getElementById('submitButton');
    // 모든 값이 선택되었으면 활성화
    if (selectedDoctorId && selectedDate && selectedTime) {
        button.disabled = false;
    }
    else {
        button.disabled = true;
    }
}
// 결과 메시지 표시
function showResult(message, isSuccess) {
    const resultDiv = document.getElementById('result');
    resultDiv.textContent = message;
    resultDiv.className = isSuccess ? 'result success' : 'result error';
}
// 오늘 날짜 문자열 반환 (YYYY-MM-DD 형식)
function getTodayString() {
    const today = new Date();
    const year = today.getFullYear();
    const month = String(today.getMonth() + 1).padStart(2, '0');
    const day = String(today.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}
// 휴무일 영어 → 한글 변환
function translateDayOff(dayOff) {
    const days = {
        'MONDAY': '월요일',
        'TUESDAY': '화요일',
        'WEDNESDAY': '수요일',
        'THURSDAY': '목요일',
        'FRIDAY': '금요일',
        'SATURDAY': '토요일',
        'SUNDAY': '일요일'
    };
    return days[dayOff] || dayOff;
}
// 진료과 이름 가져오기 헬퍼 함수
function getDepartmentName(deptId) {
    const select = document.getElementById('departmentSelect');
    const option = select.querySelector(`option[value="${deptId}"]`);
    return option?.textContent || '알 수 없음';
}
// 의사 이름 가져오기 헬퍼 함수
function getDoctorName(doctorId) {
    const card = document.querySelector(`[data-doctor-id="${doctorId}"]`);
    const nameDiv = card?.querySelector('.doctor-name');
    return nameDiv?.textContent || '알 수 없음';
}
// 날짜 포맷팅 헬퍼 함수
function formatDate(dateStr) {
    const date = new Date(dateStr);
    const days = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
    const dayOfWeek = days[date.getDay()];
    return `${dateStr} (${dayOfWeek})`;
}
// ===== 8. 초기화 (페이지 로드 시 실행)
document.addEventListener('DOMContentLoaded', async () => {
    console.log('페이지 로드 완료');
    // 진료과 목록 가져와서 드롭다운 채우기
    const departments = await fetchDepartments();
    populateDepartments(departments);
    // 진료과 선택 이벤트 연결
    const departmentSelect = document.getElementById('departmentSelect');
    departmentSelect.addEventListener('change', (e) => {
        const target = e.target;
        handleDepartmentChange(target.value);
    });
    // 날짜 선택 이벤트 연결
    const dateInput = document.getElementById('dateInput');
    dateInput.addEventListener('change', (e) => {
        const target = e.target;
        handleDateChange(target.value);
    });
    // 예약하기 버튼 이벤트 연결
    const submitButton = document.getElementById('submitButton');
    submitButton.addEventListener('click', handleSubmit);
    console.log('이벤트 연결 완료');
});