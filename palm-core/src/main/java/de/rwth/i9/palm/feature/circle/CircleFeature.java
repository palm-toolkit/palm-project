package de.rwth.i9.palm.feature.circle;

/**
 * Factory interfaces, containing all features on Circle object
 * 
 * @author sigit
 */
public interface CircleFeature
{
	public CircleAcademicEventTree getCircleAcademicEventTree();

	public CircleApi getCircleApi();

	public CircleBasicInformation getCircleBasicInformation();

	public CircleDetail getCircleDetail();

	public CircleInterest getCircleInterest();

	public CircleManage getCircleManage();

	public CirclePublication getCirclePublication();

	public CircleResearcher getCircleResearcher();

	public CircleSearch getCircleSearch();

	public CircleTopicModeling getCircleTopicModeling();

	public CircleTopPublication getCircleTopPublication();

}
