/*******************************************************************************
 * Copyright (c) 2012 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.tabris.interaction;

import static com.eclipsesource.tabris.internal.Constants.PROPERTY_LATITUDE;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_LONGITUDE;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_QUERY;
import static com.eclipsesource.tabris.internal.Constants.PROPERTY_ZOOM;
import static com.eclipsesource.tabris.internal.Preconditions.checkArgumentNotNullAndNotEmpty;


/**
 * <p>
 * Concrete launch option to open coordinates or address in the Maps App.
 * <p>
 *
 * @since 0.9
 */
public class MapsOptions extends LaunchOptions {

  public MapsOptions( double latitude, double longitude ) {
    super( App.MAPS );
    add( PROPERTY_LATITUDE, String.valueOf( latitude ) );
    add( PROPERTY_LONGITUDE, String.valueOf( longitude ) );
  }

  public MapsOptions( double latitude, double longitude, int zoomLevel ) {
    this( latitude, longitude );
    validateZoom( zoomLevel );
    add( PROPERTY_ZOOM, String.valueOf( zoomLevel ) );
  }

  public MapsOptions( String query ) {
    super( App.MAPS );
    checkArgumentNotNullAndNotEmpty( query, "Query" );
    add( PROPERTY_QUERY, query );
  }

  public MapsOptions( String query, int zoomLevel ) {
    this( query );
    validateZoom( zoomLevel );
    add( PROPERTY_ZOOM, String.valueOf( zoomLevel ) );
  }

  private void validateZoom( int zoomLevel ) {
    if( zoomLevel < 0 ) {
      throw new IllegalArgumentException( "Zoomlevel must not be negative" );
    }
  }
}
