package fr.paris.lutece.plugins.extend.modules.follow.service;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * IFollowListener
 *
 */
public interface IFollowListener {
	
	/**
	 * Notify new follow
	 * @param strExtendableResourceType the  extendable resource type   
	 * @param strIdExtendableResource the str id extendable resource
	 * @param request httpServletRequest
	 */
	void follow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request );
	
	/**
	 * Cancel Notify new follow
	 * @param strExtendableResourceType the  extendable resource type   
	 * @param strIdExtendableResource the str id extendable resource
	 * @param request httpServletRequest
	 */
	void cancelFollow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request );
	
}
