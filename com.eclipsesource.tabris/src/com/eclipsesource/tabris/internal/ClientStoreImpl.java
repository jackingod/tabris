/*******************************************************************************
 * Copyright (c) 2013 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.internal;

import static com.eclipsesource.tabris.internal.Constants.TYPE_CLIENT_STORE;
import static com.eclipsesource.tabris.internal.Constants.METHOD_ADD;
import static com.eclipsesource.tabris.internal.Constants.METHOD_CLEAR;
import static com.eclipsesource.tabris.internal.Constants.METHOD_REMOVE;
import static com.eclipsesource.tabris.internal.Constants.METHOD_SYNCHRONIZE;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_KEY;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_KEYS;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_VALUE;
import static com.eclipsesource.tabris.internal.Preconditions.checkArgumentNotNull;
import static com.eclipsesource.tabris.internal.Preconditions.checkArgumentNotNullAndNotEmpty;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.ConnectionImpl;
import org.eclipse.rap.rwt.remote.AbstractOperationHandler;
import org.eclipse.rap.rwt.remote.RemoteObject;

import com.eclipsesource.tabris.ClientStore;


@SuppressWarnings("restriction")
public class ClientStoreImpl extends AbstractOperationHandler implements ClientStore {

  private final RemoteObject serviceObject;
  private final Map<String, String> store;

  public ClientStoreImpl() {
    ConnectionImpl connection = ( ConnectionImpl )RWT.getUISession().getConnection();
    serviceObject = connection.createServiceObject( TYPE_CLIENT_STORE );
    serviceObject.setHandler( this );
    store = new HashMap<String, String>();
  }

  @Override
  public void add( String key, String value ) {
    checkArgumentNotNullAndNotEmpty( key, "Key" );
    checkArgumentNotNull( value, "Value" );
    store.put( key, value );
    sendAdd( key, value );
  }

  private void sendAdd( String key, String value ) {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( PROPERTY_KEY, key );
    properties.put( PROPERTY_VALUE, value );
    serviceObject.call( METHOD_ADD, properties );
  }

  @Override
  public String get( String key ) {
    return store.get( key );
  }

  @Override
  public void remove( String... keys ) {
    for( String key : keys ) {
      store.remove( key );
    }
    sendRemoveKeys( keys );
  }

  private void sendRemoveKeys( String[] keys ) {
    if( keys.length > 0 ) {
      Map<String, Object> properties = new HashMap<String, Object>();
      properties.put( PROPERTY_KEYS, keys );
      serviceObject.call( METHOD_REMOVE, properties );
    }
  }

  @Override
  public void clear() {
    store.clear();
    serviceObject.call( METHOD_CLEAR, null );
  }

  @Override
  public void handleCall( String method, Map<String, Object> parameters ) {
    if( method.equals( METHOD_SYNCHRONIZE ) ) {
      for( Entry<String, Object> entry : parameters.entrySet() ) {
        store.put( entry.getKey(), ( String )entry.getValue() );
      }
    }
  }

}
