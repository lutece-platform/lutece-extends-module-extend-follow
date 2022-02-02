/*
 * Copyright (c) 2002-2021, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.extend.modules.follow.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceUser;

public class FollowListenerService
{

    private static Map<String, List<IFollowListener>> _mapListeners = new HashMap< >( );
    private static boolean _bHasListeners;

    /**
     * Private constructor
     */
    private FollowListenerService() {
    	
    }
    /**
     * Register a comment listener.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type associated with the listener. Use {@link #CONSTANT_EVERY_EXTENDABLE_RESOURCE_TYPE} to associated the listener
     *            with every resource type.
     * @param listener
     *            The listener to register
     */
    public static synchronized void registerListener( String strExtendableResourceType, IFollowListener listener )
    {
        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners == null )
        {
            listListeners = new ArrayList< >( );
            _mapListeners.put( strExtendableResourceType, listListeners );
        }
        listListeners.add( listener );
        _bHasListeners = true;
    }

    /**
     * Check if there is listeners to notify
     * 
     * @return True if there is at last one listener, false otherwise
     */
    public static boolean hasListener( )
    {
        return _bHasListeners;
    }

    /**
     * Notify to listeners new follow. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param request
     *            the HTTP request
     */
    public static void follow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( IFollowListener listener : listListeners )
            {
                listener.follow( strExtendableResourceType, strIdExtendableResource, request );
            }
        }

    }

    /**
     * Notify to listeners canceled follow. Only listeners associated with the extendable resource type of the comment are notified.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id of the comment
     * @param request
     *            the HTTP request
     */
    public static void cancelFollow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( IFollowListener listener : listListeners )
            {
                listener.cancelFollow( strExtendableResourceType, strIdExtendableResource, request );
            }
        }

    }

    public static boolean canFollow( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user )
    {
        List<IFollowListener> listListeners = _mapListeners.get( strExtendableResourceType );
        if ( listListeners != null )
        {
            for ( IFollowListener listener : listListeners )
            {
            	if( !listener.canFollow( strExtendableResourceType, strIdExtendableResource, user )) {
            		return false;
            	}
            }
        }
        return true;
    }

}
