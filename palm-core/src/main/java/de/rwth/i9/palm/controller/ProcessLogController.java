package de.rwth.i9.palm.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import de.rwth.i9.palm.service.ApplicationService;

@Controller
@RequestMapping( value = "/log" )
public class ProcessLogController
{

	@Autowired
	private ApplicationService applicationService;

	@RequestMapping( value = "/process", method = RequestMethod.GET )
	public @ResponseBody String getLogMessage( @RequestParam( value = "pid" ) final String pid) throws InterruptedException
	{
		return applicationService.getProcessLog( pid );
	}

}