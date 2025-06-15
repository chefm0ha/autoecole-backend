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

import com.springBoot.autoEcole.model.Monitor;
import com.springBoot.autoEcole.service.MonitorService;
import com.sun.istack.NotNull;
@RestController
@RequestMapping("/monitor")
@CrossOrigin
public class MonitorFacade {

	@Autowired
	private MonitorService monitorService;
	
	@GetMapping("/getMonitors") 
	public Collection<Monitor> getMonitors() {
		return  monitorService.findAllMonitor();	
	}
	
	@PostMapping("/saveMonitor") 
	public Monitor saveMonitor(@RequestBody Monitor monitor) {
		return  monitorService.saveMonitor(monitor);			
	}
	@GetMapping("/getMonitor/{cin}") 
	public Monitor getMonitorByImmat(@PathVariable @NotNull String cin) {
		return  monitorService.findByCin(cin);
	}
	
	@GetMapping("/deleteMonitor/{cin}") 
	public Long deleteMonitor(@PathVariable @NotNull String cin) {
		return  monitorService.deleteMonitor(cin);			
	}
}
