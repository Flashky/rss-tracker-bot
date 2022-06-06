package com.flashk.bots.rsstracker.controllers.constants;

public final class PathConstants {

	// Query parameters
	public static final String QUERY_ACTION = "action";
	public static final String QUERY_PAGE = "page";
	public static final String QUERY_SIZE = "size";
	
	// Uri paths
	public static final String URI_FEED = "/feeds/{feedId}?action={action}";
	public static final String URI_FEEDS = "/feeds?page={page}&size={size}";
	
	private PathConstants() {}
}
