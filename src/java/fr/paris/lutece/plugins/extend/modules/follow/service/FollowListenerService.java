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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

import fr.paris.lutece.portal.service.security.LuteceUser;

/**
 * Notifies the follow listeners: the ones registered for a resource type, and every {@link IFollowListener} CDI bean.
 */
public final class FollowListenerService
{
    private static final Map<String, List<IFollowListener>> _mapListeners = new ConcurrentHashMap<>( );

    /**
     * Private constructor
     */
    private FollowListenerService( )
    {
    }

    /**
     * Register a follow listener.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type associated with the listener
     * @param listener
     *            The listener to register
     */
    public static void registerListener( String strExtendableResourceType, IFollowListener listener )
    {
        _mapListeners.computeIfAbsent( strExtendableResourceType, strType -> new CopyOnWriteArrayList<>( ) ).add( listener );
    }

    /**
     * Check if there is listeners to notify
     * 
     * @return True if there is at last one listener, false otherwise
     */
    public static boolean hasListener( )
    {
        return !_mapListeners.isEmpty( ) || !CDI.current( ).select( IFollowListener.class ).isUnsatisfied( );
    }

    /**
     * Notify to listeners new follow. Only listeners associated with the extendable resource type are notified, with
     * every listener bean.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id
     * @param request
     *            the HTTP request
     */
    public static void follow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        getListeners( strExtendableResourceType ).forEach( listener -> listener.follow( strExtendableResourceType, strIdExtendableResource, request ) );
    }

    /**
     * Notify to listeners canceled follow. Only listeners associated with the extendable resource type are notified,
     * with every listener bean.
     * 
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id
     * @param request
     *            the HTTP request
     */
    public static void cancelFollow( String strExtendableResourceType, String strIdExtendableResource, HttpServletRequest request )
    {
        getListeners( strExtendableResourceType ).forEach( listener -> listener.cancelFollow( strExtendableResourceType, strIdExtendableResource, request ) );
    }

    /**
     * Asks the listeners whether a user may follow a resource.
     *
     * @param strExtendableResourceType
     *            The extendable resource type
     * @param strIdExtendableResource
     *            The extendable resource id
     * @param user
     *            the user
     * @return true when no listener refuses
     */
    public static boolean canFollow( String strExtendableResourceType, String strIdExtendableResource, LuteceUser user )
    {
        return getListeners( strExtendableResourceType ).allMatch( listener -> listener.canFollow( strExtendableResourceType, strIdExtendableResource, user ) );
    }

    /**
     * Gives the listeners registered for a resource type, then the listener beans.
     *
     * @param strExtendableResourceType
     *            The extendable resource type
     * @return the listeners
     */
    private static Stream<IFollowListener> getListeners( String strExtendableResourceType )
    {
        List<IFollowListener> listRegistered = ( strExtendableResourceType != null ) ? _mapListeners.get( strExtendableResourceType ) : null;
        Stream<IFollowListener> registered = ( listRegistered != null ) ? listRegistered.stream( ) : Stream.empty( );

        return Stream.concat( registered, CDI.current( ).select( IFollowListener.class ).stream( ) );
    }
}
