package com.studentinformation.service;


import com.studentinformation.domain.*;
import com.studentinformation.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GradeService {

    private final GradeRepository gradeRepository;
    private final LectureRepository lectureRepository;

    @Transactional
    public Grade createEmptyGrade(Member student, Lecture lecture) {
        Grade emptyGrade = Grade.createEmptyGrade(student, lecture);
        return gradeRepository.save(emptyGrade);
    }

    @Transactional
    public Grade saveGrade(Grade grade){
        return gradeRepository.save(grade);
    }


    public Grade findGradeById(Long gradeId){
        return gradeRepository.findById(gradeId).get();
    }

    /**
     * 성적 입력/수정 가능
     */
    @Transactional
    public Grade editGradeOfScore(Long gradeId, Score score){
        Grade grade = gradeRepository.findById(gradeId).get();//확실하게 존재함
        grade.updateGrade(score);
        return grade;
    }

    /**
     * Grade 리스트, Score 리스트 하나하나 매칭해서 성적 입력
     */
    @Transactional
    public void editGradeListOfScore(Long lectureId, List<Score> scores){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("not exits lectureId"));
        List<Grade> grades = lecture.getGrades();
        for(int i = 0 ; i < grades.size(); i++){
            Grade grade = grades.get(i);
            editGradeOfScore(grade.getId(),scores.get(i));
        }
    }

    /**
     * 이의 내용 입력/수정 가능
     */
    @Transactional
    public Grade editGradeOfObjection(Long gradeId, String objection){
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new IllegalArgumentException("not exist gradeId"));
        if(StringUtils.hasText(objection)) {
            grade.updateObjection(objection);
            log.info("update grade objection = {}",grade);
        }
        return grade;
    }


    /**
     * 이의제기하는 글만 반환
     */
    public Page<Grade> findAllExistObjection(Long lectureId, Pageable pageable){
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new IllegalArgumentException("not exits lectureId"));
        List<Grade> grades = lecture.getGrades();
        List<Grade> objectionsList = grades.stream()
                .filter(grade -> StringUtils.hasText(grade.getObjection()))// null, 공백이면 false
                .filter(grade -> !grade.isObjectionCheck())
                .collect(Collectors.toList());
        int page = (pageable.getPageNumber() == 0) ? 0 : (pageable.getPageNumber() - 1);
        pageable = PageRequest.of(page, 10); // <- Sort 추가

        int start = (int) pageable.getOffset();
        int end = Math.min(start+pageable.getPageSize(),objectionsList.size());
        return new PageImpl<>(objectionsList.subList(start,end),pageable,objectionsList.size());
    }

    public boolean checkInaccessibleGradeWithProfessor(Member professor,Long gradeId){
        boolean access = professor.getGrades().stream()
                .anyMatch(grade -> grade.getId().equals(gradeId));
        return !access;
    }
}
