package de.rwth.i9.palm.wrapper;

import java.util.List;

import de.rwth.i9.palm.model.ExtractionService;

public class ExtractionServiceListWrapper
{
	private List<ExtractionService> extractionServices;

	public List<ExtractionService> getExtractionServices()
	{
		return extractionServices;
	}

	public void setExtractionServices( List<ExtractionService> extractionServices )
	{
		this.extractionServices = extractionServices;
	}

}
