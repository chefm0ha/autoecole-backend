package com.springBoot.autoEcole.service.impl;

import java.util.Collection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.springBoot.autoEcole.model.Instructor;
import com.springBoot.autoEcole.repository.InstructorDao;
import com.springBoot.autoEcole.service.InstructorService;

@Service
@Transactional
public class InstructorServiceImpl implements InstructorService {

	@Autowired
	private InstructorDao instructorDao;

	@Override
	public Collection<Instructor> findAllInstructor() {
		return (Collection<Instructor>) instructorDao.findAll();
	}

	@Override
	public Instructor saveInstructor(Instructor instructor) {
		return instructorDao.save(instructor);
	}

	@Override
	public Instructor findByCin(String cin) {
		return instructorDao.findByCin(cin);
	}

	@Override
	public Long deleteInstructor(String cin) {
		return instructorDao.removeByCin(cin);
	}
}