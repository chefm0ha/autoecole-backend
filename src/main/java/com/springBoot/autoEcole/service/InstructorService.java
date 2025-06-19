package com.springBoot.autoEcole.service;

import java.util.Collection;
import com.springBoot.autoEcole.model.Instructor;

public interface InstructorService {
	Collection<Instructor> findAllInstructor();
	Instructor saveInstructor(Instructor instructor);
	Instructor findByCin(String cin);
	Long deleteInstructor(String cin);
}