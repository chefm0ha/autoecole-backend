package com.autoecole.service;

import java.util.Collection;
import com.autoecole.model.Instructor;

public interface InstructorService {
	Collection<Instructor> findAllInstructor();
	Instructor saveInstructor(Instructor instructor);
	Instructor findByCin(String cin);
	Long deleteInstructor(String cin);
}