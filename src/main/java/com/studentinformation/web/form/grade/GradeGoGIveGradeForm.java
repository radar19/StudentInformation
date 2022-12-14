package com.studentinformation.web.form.grade;

import com.studentinformation.domain.Grade;
import com.studentinformation.domain.Lecture;
import com.studentinformation.domain.Member;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class GradeGoGIveGradeForm {

    private List<Lecture> lectureList;
    private List<Grade> gradeList;



    public static GradeGoGIveGradeForm makeForm(Member professor){
        return new GradeGoGIveGradeForm(professor);
    }

    public static GradeGoGIveGradeForm makeFormWithLectureId(Member professor, Lecture lecture){
        return new GradeGoGIveGradeForm(professor,lecture);
    }

    private GradeGoGIveGradeForm(Member professor){
        this.lectureList = professor.getProfessorLectures();
    }

    private GradeGoGIveGradeForm(Member professor,Lecture lecture){
        this.lectureList = professor.getProfessorLectures();
        this.gradeList = lecture.getGrades();
    }
}
