package com.autoecole.service.impl;

import java.util.Collection;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.autoecole.model.Instructor;
import com.autoecole.repository.InstructorDao;
import com.autoecole.service.InstructorService;

@Service
@Transactional
@AllArgsConstructor
public class InstructorServiceImpl implements InstructorService {

	private final InstructorDao instructorDao;

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