package de.rwth.i9.palm.datasetcollect.service;

public class HtmlSelectorConstant
{
	// ================ Google Scholar ===================
	// first phase get the author list
	public static final String GS_AUTHOR_LIST_CONTAINER = ".gsc_1usr";
	public static final String GS_AUTHOR_LIST_NAME = ".gsc_1usr_name";
	public static final String GS_AUTHOR_LIST_AFFILIATION = ".gsc_1usr_aff";
	public static final String GS_AUTHOR_LIST_NOCITATION = ".gsc_1usr_cby";

	// second phase get the publication list
	public static final String GS_PUBLICATION_ROW_LIST = "tr.gsc_a_tr";
	public static final String GS_PUBLICATION_COAUTHOR_AND_VENUE = ".gs_gray";
	public static final String GS_PUBLICATION_NOCITATION = ".gsc_a_c";
	public static final String GS_PUBLICATION_DATE = ".gsc_a_y";
	public static final String GS_INDICES_ROW_LIST = "#gsc_rsb_st tr";

	// third phase get the publication details
	public static final String GS_PUBLICATION_DETAIL_CONTAINER = "#gs_ccl";
	public static final String GS_PUBLICATION_DETAIL_TITLE = "#gsc_title";
	public static final String GS_PUBLICATION_DETAIL_PDF = ".gsc_title_ggi";
	public static final String GS_PUBLICATION_DETAIL_PROP = ".gs_scl";
	public static final String GS_PUBLICATION_DETAIL_PROP_LABEL = ".gsc_field";
	public static final String GS_PUBLICATION_DETAIL_PROP_VALUE = ".gsc_value";

	// ================ CiteSeerX ===================
	// first phase get the author list
	public static final String CSX_AUTHOR_LIST = ".result";
	public static final String CSX_AUTHOR_ROW_DETAIL = ".authInfo";

	// second phase get the publication list
	public static final String CSX_PUBLICATION_ROW_LIST = "table.refs tr";
	public static final String CSX_PUBLICATION_COAUTHOR_AND_VENUE = ".gs_gray";
	public static final String CSX_PUBLICATION_NOCITATION = ".gsc_a_c";
	public static final String CSX_PUBLICATION_YEAR = ".gsc_a_y";

	// third phase get the publication details
	public static final String CSX_PUBLICATION_DETAIL_HEADER = "#viewHeader";
	public static final String CSX_PUBLICATION_DETAIL_DOWNLOAD = "#downloads";
	public static final String CSX_PUBLICATION_DETAIL_COAUTHOR = "#docAuthors";
	public static final String CSX_PUBLICATION_DETAIL_VENUE = "#docVenue";
	public static final String CSX_PUBLICATION_DETAIL_ABSTRACT = "#abstract";
	public static final String CSX_PUBLICATION_DETAIL_CITATION = "#citations";

	// ================ DBLP ===================
	// first phase get the author list
	public static final String DBLP_AUTHOR_LIST = ".result";
	public static final String DBLP_AUTHOR_ROW_DETAIL = ".authInfo";

	// second phase get the publication list
	public static final String DBLP_PUBLICATION_ROW_LIST = "table.refs tr";
	public static final String DBLP_PUBLICATION_COAUTHOR_AND_VENUE = ".gs_gray";
	public static final String DBLP_PUBLICATION_NOCITATION = ".gsc_a_c";
	public static final String DBLP_PUBLICATION_YEAR = ".gsc_a_y";

	// third phase get the publication details
	public static final String DBLP_PUBLICATION_DETAIL_HEADER = "#viewHeader";
	public static final String DBLP_PUBLICATION_DETAIL_DOWNLOAD = "#downloads";
	public static final String DBLP_PUBLICATION_DETAIL_COAUTHOR = "#docAuthors";
	public static final String DBLP_PUBLICATION_DETAIL_VENUE = "#docVenue";
	public static final String DBLP_PUBLICATION_DETAIL_ABSTRACT = "#abstract";
	public static final String DBLP_PUBLICATION_DETAIL_CITATION = "#citations";

	// ================ MENDELEY ===================
	// first phase get the author list
	public static final String MDY_AUTHOR_ROW_DETAIL = ".summary-left";
	public static final String MDY_AUTHOR_STATISTICS_ROW_DETAIL = ".statistics-content";
}
