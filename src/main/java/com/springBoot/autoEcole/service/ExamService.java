package com.springBoot.autoEcole.service;

import java.util.Collection;

import com.springBoot.autoEcole.bean.ExamReportingBean;
import com.springBoot.autoEcole.model.Exam;

public interface ExamService {
	public Exam saveExam(String id, Exam exam);

	public Long deleteExam(Long id);

	public Collection<ExamReportingBean> getExamOnPeriod(Integer period);

	public Integer getCountExamOnWeekByType(String typeExam);
	
	public Float getSuccessRateExamCode();

	Float getSuccessRateExamConduite();

}
