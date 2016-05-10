package fr.paris.lutece.plugins.extend.modules.follow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.extend.modules.follow.service.IFollowListener;
import fr.paris.lutece.portal.service.security.LuteceUser;

public class FollowListenerService {
	  
	private static Map<String, List<IFollowListener>> _mapListeners = new HashMap<String, List<IFollowListener>>( );
	  private static boolean _bHasListeners;
	  
	  
	  /**
	     * Register a comment listener.
	     * @param strExtendableResourceType The extendable resource type associated
	     *            with the listener. Use
	     *            {@link #CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE} to associated
	     *            the listener with every resource type.
	     * @param listener The listener to register
	     */
	    public static synchronized void registerListener( String strExtendableResourceType, IFollowListener listener )
	    {
	        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
	        if ( listListeners == null )
	        {
	            listListeners = new ArrayList<IFollowListener>( );
	            _mapListeners.put( strExtendableResourceType, listListeners );
	        }
	        listListeners.add( listener );
	        _bHasListeners = true;
	    }

	    /**
	     * Check if there is listeners to notify
	     * @return True if there is at last one listener, false otherwise
	     */
	    public static boolean hasListener( )
	    {
	        return _bHasListeners;
	    }
	    
	    
	    
	    /**
	     * Notify to listeners new follow. Only listeners associated
	     * with the extendable resource type of the comment are notified.
	     * @param strExtendableResourceType The extendable resource type
	     * @param strIdExtendableResource The extendable resource id of the comment
	     * @param request the HTTP request
	     */
	    public static void follow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
	    {
	        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
	        if ( listListeners != null )
	        {
	            for ( IFollowListener listener : listListeners )
	            {
	                listener.follow(strExtendableResourceType, strIdExtendableResource, request);
	            }
	        }
	       
	    }
	    
	    /**
	     * Notify to listeners canceled follow. Only listeners associated
	     * with the extendable resource type of the comment are notified.
	     * @param strExtendableResourceType The extendable resource type
	     * @param strIdExtendableResource The extendable resource id of the comment
	     * @param request the HTTP request
	     */
	    public static void cancelFollow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
	    {
	        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
	        if ( listListeners != null )
	        {
	            for ( IFollowListener listener : listListeners )
	            {
	                listener.cancelFollow(strExtendableResourceType, strIdExtendableResource, request);
	            }
	        }
	       
	    }
	    

	    public static boolean canFollow( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user )
	    {
	    	 List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
		     boolean res = false;   
	    	 if ( listListeners != null )
		        {
		            for ( IFollowListener listener : listListeners )
		            {
		            	return listener.canFollow(strExtendableResourceType, strIdExtendableResource, user);
		            }
		        }
	    	 return res ;
	    }

}
