package com.studentinformation.service;


import com.studentinformation.domain.Member;
import com.studentinformation.repository.MemberRepository;
import com.studentinformation.web.form.member.ChangePasswordForm;
import com.sun.jdi.request.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 비밀번호 암호화 해야함
    @Transactional
    public Member addMember(Member member){
        member.encodePassword(member.getPassword(), bCryptPasswordEncoder);
        memberRepository.save(member);
        log.info("addMember = {}",member);
        return member;
    }

    /**  개인정보 갱신할때 넘겨주는 newMember 데이터는 controller에서 DTO를 entity로 변환한거임
     * 이거 학생이 바꾸는게 아니라 Admin전용, Admin이 학생 정보 바꿀 떄(휴학, 전과) 사용
     * @param oldMemberId
     * @param newMember
     */
    @Transactional
    public Member update(Long oldMemberId, Member newMember){
        Member findMember = findById(oldMemberId);
        findMember.update(newMember,bCryptPasswordEncoder);
        log.info("updateMember = {}",findMember);
        return findMember;
    }

    //비밀번호 변경
    @Transactional
    public Member updatePassword(Member member, ChangePasswordForm form){
        if (!bCryptPasswordEncoder.matches(form.getPrePassword(), member.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호 오류");
        }

        if (bCryptPasswordEncoder.matches(form.getNewPassword(), member.getPassword())) {
            throw new DuplicateRequestException("newPassword is duplicated oldPassword");
        }

        log.info("changePassword = {}, oldPassword= {}",form.getNewPassword() ,form.getPrePassword());
        member.changePassword(form.getNewPassword(), bCryptPasswordEncoder);
        return member;
    }

    //아이디 찾을 때 사용
    public String findStudentNum(String memberName){
        Member findMember = memberRepository.findByMemberName(memberName)
                .orElseThrow(() -> new IllegalArgumentException("not found memberName data"));
        return findMember.getStudentNum();
    }

    /**
     * 비밀번호 찾을 떄 학생이름,학생번호 사용해서 찾음
     * @param memberName
     * @param memberNum
     */
    public String findPassword(String memberName,String memberNum){
        Member findMember = findByMemberNum(memberNum);
        String name = findMember.getMemberName();
        if(name.equals(memberName)){
            return findMember.getPassword();
        }else{
            throw new IllegalArgumentException("not match memberName with studentNum data");
        }
    }

    public Member login(String studentNum, String password) {
        return memberRepository.findByStudentNum(studentNum)
                .filter(member -> member.getPassword().equals(password))
                .orElse(null);
    }


    public Member findById(Long id){
        return memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("no such data"));
    }

    public Member findByMemberNum(String memberNum){
        return memberRepository.findByStudentNum(memberNum)
                .orElseThrow(() -> new IllegalArgumentException("not found studentNum data"));
    }

}
