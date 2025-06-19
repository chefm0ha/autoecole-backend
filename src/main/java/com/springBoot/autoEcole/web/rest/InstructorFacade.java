package com.springBoot.autoEcole.web.rest;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springBoot.autoEcole.model.Instructor;
import com.springBoot.autoEcole.service.InstructorService;

@RestController
@RequestMapping("/instructor")
@CrossOrigin
public class InstructorFacade {

	@Autowired
	private InstructorService instructorService;

	@GetMapping("/getInstructors")
	public Collection<Instructor> getInstructors() {
		return instructorService.findAllInstructor();
	}

	@PostMapping("/saveInstructor")
	public Instructor saveInstructor(@RequestBody Instructor instructor) {
		return instructorService.saveInstructor(instructor);
	}

	@GetMapping("/getInstructor/{cin}")
	public Instructor getInstructorByCin(@PathVariable String cin) {
		return instructorService.findByCin(cin);
	}

	@GetMapping("/deleteInstructor/{cin}")
	public Long deleteInstructor(@PathVariable String cin) {
		return instructorService.deleteInstructor(cin);
	}
}