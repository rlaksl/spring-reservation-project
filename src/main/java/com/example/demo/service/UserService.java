package com.example.demo.service;

import com.example.demo.repository.UserRepository;
import com.example.demo.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service // Spring Bean으로 등록
@RequiredArgsConstructor // final 필드를 사용하여 생성자 자동 생성 및 의존성 주입
@Transactional(readOnly = true) // 트랜잭션의 기본 설정을 읽기 전용으로 지정
public class UserService {

    private final UserRepository userRepository; // UserRepository 인터페이스 주입

    // 회원 가입 로직 (데이터 변경)
    @Transactional
    public Long Join(User user) {
        userRepository.save(user);
        return user.getId();
    }

    // ID로 사용자 조회
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                             .orElseThrow(() -> new IllegalStateException("사용자가 존재하지 않습니다."));
    }

    // 이메일로 사용자 조회
    public Optional<User> findUserByEmail(String email) {
        // 이 부분은 UserRepository 인터페이스에 'findByEmail(String email)' 메서드를 추가하면 됩니다.
        return Optional.empty(); // 임시 반환

    }

}
