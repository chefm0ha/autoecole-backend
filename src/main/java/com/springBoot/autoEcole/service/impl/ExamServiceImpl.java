package com.springBoot.autoEcole.service.impl;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springBoot.autoEcole.model.Candidate;
import com.springBoot.autoEcole.model.Exam;
import com.springBoot.autoEcole.repository.ExamDao;
import com.springBoot.autoEcole.service.CandidateService;
import com.springBoot.autoEcole.service.ExamService;

@Service
@Transactional
public class ExamServiceImpl implements ExamService{

	@Autowired
	private CandidateService candidateService;
	@Autowired
	private ExamDao examDao;
	
	@Override
	public Exam saveExam(String id, Exam exam) {
		Candidate candidate = candidateService.findByCin(id);
		Exam examC = new Exam();
		if("Code".equals(exam.getTypeExam()) && examDao.countByCandidatAndTypeExam(candidate, "Code") ==0) {
		    examC.setFirstExam(true);
		}
		else if("Conduite".equals(exam.getTypeExam()) && examDao.countByCandidatAndTypeExam(candidate, "Conduite") ==0) {
			 examC.setFirstExam(true);
		}
		examC.setCandidate(candidate);
		examC.setDateExam(exam.getDateExam());
		examC.setResult(exam.getResult());
		examC.setTypeExam(exam.getTypeExam());
		examDao.save(examC);
		return examC;
	}

	@Override
	public Long deleteExam(Long id) {
		return examDao.removeById(id);
	}

	@Override
	public Collection<ExamReportingBean> getExamOnPeriod(Integer period) {
		List<ExamReportingBean> reportingExams= new ArrayList<ExamReportingBean>();
		Instant now =Instant.now();
		Date today= Date.from(now);
		Date compareDate =Date.from(now.plus(Duration.ofDays(period)));
		for(Exam exam:examDao.findExamOnPeriod(today, compareDate)) {
			ExamReportingBean reportingExam = new ExamReportingBean();
			reportingExam.setFullName(exam.getCandidate().getLastName() +" "+exam.getCandidate().getFirstName());
			reportingExam.setCategory(exam.getCandidate().getCategory());
			reportingExam.setDateExam(exam.getDateExam());
			reportingExam.setTypeExam(exam.getTypeExam());
			reportingExams.add(reportingExam);
		}
		return reportingExams;
	}

	@Override
	public Integer getCountExamOnWeekByType(String typeExam) {
		Instant now =Instant.now();
		Date today= Date.from(now);
		Date weekDate =Date.from(now.plus(Duration.ofDays(7)));
		return examDao.getCountExamOnWeekByType(typeExam,today, weekDate);
	}

	@Override
	public Float getSuccessRateExamCode() {
		Float rateSuccessCode=(float) 0;
		 if(examDao.getCountExamCodeValid() !=0 && examDao.getcountByTypeExam("Code")!=0) {
			 rateSuccessCode=(examDao.getCountExamCodeValid() /examDao.getcountByTypeExam("Code"))*100;
		 }
		return rateSuccessCode;
	}
	
	@Override
	public Float getSuccessRateExamConduite() {
		Float rateSuccessConduite=(float)0;
		 if(examDao.getCountExamConduiteValid() !=0 && examDao.getcountByTypeExam("Conduite")!=0) {
			 rateSuccessConduite=(examDao.getCountExamConduiteValid() / examDao.getcountByTypeExam("Conduite"))*100;
		 }
		return rateSuccessConduite;
	}

}
