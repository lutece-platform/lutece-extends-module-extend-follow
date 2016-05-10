package fr.paris.lutece.plugins.extend.modules.follow.service;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceUser;

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
	
    /**
     * Can Follow
     * @param startDate the start date
     * @param endDate the end date
     * @return boolean if can Follow
     */
	boolean canFollow( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user ) ;
}
