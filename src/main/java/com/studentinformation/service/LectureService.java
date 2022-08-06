package com.studentinformation.service;


import com.studentinformation.domain.Lecture;
import com.studentinformation.domain.Member;
import com.studentinformation.domain.MemberState;
import com.studentinformation.repository.LectureRepository;
import com.studentinformation.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LectureService {

    private final LectureRepository lectureRepository;
    private final MemberRepository memberRepository;

    //강의 생성
    @Transactional
    public Lecture makeLecture(Lecture lecture){
        log.info("makeLecture = {}",lecture);
        return lectureRepository.save(lecture);
    }

    //강의 수정
    @Transactional
    public Lecture editLecture(Long oldLectureId,Lecture newLecture){
        Lecture oldLecture = lectureRepository.findById(oldLectureId).get();
        log.info("oldLecture = {}",oldLecture);
        oldLecture.update(newLecture);
        log.info("newLecture = {}",oldLecture);
        return oldLecture;
    }

    //강의 삭제
    @Transactional
    public void deleteLecture(Long lectureId){
        lectureRepository.deleteById(lectureId);
    }


    /**
     * 모든 교수 중 이름에 professorName이 포함되어있으면 다 저장
     * 저장된 교수의 모든 Lecture를 Page로 모아둠
     * @param professorName
     * @param pageable
     * 나중에 쿼리로 최적화 해야할듯
     */
    public Page<Lecture> findByProfessorName(String professorName, Pageable pageable){
        List<Member> findAllProfessor = memberRepository.findByState(MemberState.professor);
        List<Member> findProfessors = findAllProfessor.stream()
                .filter(professor -> professor.getMemberName().contains(professorName))
                .collect(Collectors.toList());

        log.info("find professor contain argument, professors={}",findProfessors.stream()
                .map(Member::getMemberName)
                .collect(Collectors.toList()));

        List<Lecture> findLectures = findProfessors.stream()
                .map(Member::getProfessorLectures)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
        log.info("findLecturesSize = {}",findLectures.size());

        int start = (int)pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), findLectures.size());
        return new PageImpl<>(findLectures.subList(start,end),pageable,findLectures.size());
    }


    /**
     * 모든 강의 조회한다음, arguement로 들어온 string을 포함하는 강의 검색
     * @param lectureName
     * @param pageable
     * 나중에 쿼리로 최적화 해야할듯
     */
    public Page<Lecture> findByLectureName(String lectureName,Pageable pageable){
        List<Lecture> findLectures = lectureRepository.findAll().stream()
                .filter(lecture -> lecture.getLectureName().contains(lectureName))
                .collect(Collectors.toList());

        log.info("find lectures contain with String, Lectures = {}",findLectures.stream().
                map(Lecture::getLectureName).collect(Collectors.toList()));

        int start = (int)pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), findLectures.size());
        return new PageImpl<>(findLectures.subList(start,end),pageable,findLectures.size());
    }

    //강의 id는 단 하나여서 Lecture 하나만 반환
    public Lecture findByLectureId(Long lectureId){
        return lectureRepository.findById(lectureId).orElse(null);
    }

    public Page<Lecture> findBySemester(String semester,Pageable pageable){
        return lectureRepository.findBySemester(semester,pageable);
    }


}
