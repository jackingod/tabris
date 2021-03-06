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
package com.eclipsesource.tabris.internal;

import static com.eclipsesource.tabris.test.TabrisTestUtil.mockServiceObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.remote.RemoteObject;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.eclipsesource.tabris.geolocation.Coordinates;
import com.eclipsesource.tabris.geolocation.GeolocationListener;
import com.eclipsesource.tabris.geolocation.GeolocationOptions;
import com.eclipsesource.tabris.geolocation.Position;
import com.eclipsesource.tabris.geolocation.PositionError;
import com.eclipsesource.tabris.geolocation.PositionError.PositionErrorCode;


public class GeolocationImplTest {

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakePhase( PhaseId.PROCESS_ACTION );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( GeolocationImpl.class ) );
  }

  @Test
  public void testCoordinatesIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( Coordinates.class ) );
  }

  @Test
  public void testgeolocationListenerIsSerializable() {
    assertTrue( Serializable.class.isAssignableFrom( GeolocationListener.class ) );
  }

  @Test
  public void testInitialFlavor() {
    RemoteObject remoteObject = mockServiceObject();

    new GeolocationImpl();

    verify( remoteObject ).set( "needsPosition", "NEVER" );
  }

  @Test
  public void testSetsOptionsWhenGetLocation() {
    RemoteObject remoteObject = mockServiceObject();
    GeolocationImpl geolocation = new GeolocationImpl();
    GeolocationOptions options = new GeolocationOptions();
    GeolocationListener listener = mock( GeolocationListener.class );
    geolocation.addGeolocationListener( listener );

    geolocation.determineCurrentPosition( options );

    verify( remoteObject ).set( "needsPosition", "ONCE" );
    verify( remoteObject ).set( "frequency", options.getFrequency() );
    verify( remoteObject ).set( "maximumAge", options.getMaximumAge() );
    verify( remoteObject ).set( "highAccuracy", options.isHighAccuracyEnabled() );
  }

  @Test( expected = IllegalArgumentException.class )
  public void testGetLocationFailsWithNullOptions() {
    GeolocationImpl geolocation = new GeolocationImpl();
    GeolocationListener listener = mock( GeolocationListener.class );
    geolocation.addGeolocationListener( listener );

    geolocation.determineCurrentPosition( null );
  }

  @Test
  public void testSetsOptionsWhenWatch() {
    RemoteObject remoteObject = mockServiceObject();
    GeolocationImpl geolocation = new GeolocationImpl();
    GeolocationOptions options = new GeolocationOptions();
    GeolocationListener listener = mock( GeolocationListener.class );
    geolocation.addGeolocationListener( listener );

    geolocation.watchPosition( options );

    verify( remoteObject ).set( "needsPosition", "CONTINUOUS" );
    verify( remoteObject ).set( "frequency", options.getFrequency() );
    verify( remoteObject ).set( "maximumAge", options.getMaximumAge() );
    verify( remoteObject ).set( "highAccuracy", options.isHighAccuracyEnabled() );
  }


  @Test( expected = IllegalArgumentException.class )
  public void testWatchFailsWithNullOptions() {
    GeolocationImpl geolocation = new GeolocationImpl();

    geolocation.watchPosition( null );
  }


  @Test
  public void testSetsNeverWhenClearWatch() {
    RemoteObject remoteObject = mockServiceObject();
    GeolocationImpl geolocation = new GeolocationImpl();

    geolocation.clearWatch();

    verify( remoteObject, times( 2 ) ).set( "needsPosition", "NEVER" );
  }

  @Test
  public void testDelegatesPositionWhenGetPosition() throws ParseException {
    GeolocationListener listener = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener );
    geolocation.determineCurrentPosition( new GeolocationOptions() );
    Map<String, Object> properties = createPositionData();

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdate", properties );

    ArgumentCaptor<Position> captor = ArgumentCaptor.forClass( Position.class );
    verify( listener ).positionReceived( captor.capture() );
    assertPositionData( captor.getValue() );
  }

  @Test
  public void testDelegatesPositionWhenGetPositionToAllListeners() throws ParseException {
    GeolocationListener listener1 = mock( GeolocationListener.class );
    GeolocationListener listener2 = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener1 );
    geolocation.addGeolocationListener( listener2 );
    geolocation.determineCurrentPosition( new GeolocationOptions() );
    Map<String, Object> properties = createPositionData();

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdate", properties );

    ArgumentCaptor<Position> captor = ArgumentCaptor.forClass( Position.class );
    verify( listener1 ).positionReceived( captor.capture() );
    verify( listener2 ).positionReceived( captor.capture() );
    assertPositionData( captor.getAllValues().get( 0 ) );
    assertPositionData( captor.getAllValues().get( 1 ) );
  }

  @Test
  public void testDelegatesPositionWhenWatch() throws ParseException {
    GeolocationListener listener = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener );
    geolocation.watchPosition( new GeolocationOptions() );
    Map<String, Object> properties = createPositionData();

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdate", properties );

    ArgumentCaptor<Position> captor = ArgumentCaptor.forClass( Position.class );
    verify( listener ).positionReceived( captor.capture() );
    assertPositionData( captor.getValue() );
  }

  @Test
  public void testDelegatesPositionWhenWatchToAllListeners() throws ParseException {
    GeolocationListener listener1 = mock( GeolocationListener.class );
    GeolocationListener listener2 = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener1 );
    geolocation.addGeolocationListener( listener2 );
    geolocation.watchPosition( new GeolocationOptions() );
    Map<String, Object> properties = createPositionData();

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdate", properties );

    ArgumentCaptor<Position> captor = ArgumentCaptor.forClass( Position.class );
    verify( listener1 ).positionReceived( captor.capture() );
    verify( listener2 ).positionReceived( captor.capture() );
    assertPositionData( captor.getAllValues().get( 0 ) );
    assertPositionData( captor.getAllValues().get( 1 ) );
  }

  private Map<String, Object> createPositionData() {
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "timestamp", "2012-03-12T10:40:13+0100" );
    properties.put( "latitude", Double.valueOf( 101.1 ) );
    properties.put( "longitude", Double.valueOf( 43.1 ) );
    properties.put( "altitude", Integer.valueOf( 3 ) );
    properties.put( "accuracy", Double.valueOf( 5.1 ) );
    properties.put( "altitudeAccuracy", Double.valueOf( 1.1 ) );
    properties.put( "heading", Double.valueOf( 21.1 ) );
    properties.put( "speed", Double.valueOf( 216.1 ) );
    return properties;
  }

  private void assertPositionData( Position position ) throws ParseException {
    assertEquals( position.getTimestamp(), parseDate() );
    assertEquals( position.getCoords().getAccuracy(), 5.1, 0 );
    assertEquals( position.getCoords().getAltitude(), 3, 0 );
    assertEquals( position.getCoords().getAltitudeAccuracy(), 1.1, 0 );
    assertEquals( position.getCoords().getHeading(), 21.1, 0 );
    assertEquals( position.getCoords().getLatitude(), 101.1, 0 );
    assertEquals( position.getCoords().getLongitude(), 43.1, 0 );
    assertEquals( position.getCoords().getSpeed(), 216.1, 0 );
  }

  private Date parseDate() throws ParseException {
    return new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ssZ" ).parse( "2012-03-12T10:40:13+0100" );
  }

  @Test
  public void testDelegatesPositionErrorWhenGetPosition() {
    GeolocationListener listener = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener );
    geolocation.determineCurrentPosition( new GeolocationOptions() );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "errorCode", "UNKNOWN" );
    properties.put( "errorMessage", "A Message" );

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdateError", properties );

    ArgumentCaptor<PositionError> captor = ArgumentCaptor.forClass( PositionError.class );
    verify( listener ).errorReceived( captor.capture() );
    assertEquals( PositionErrorCode.UNKNOWN, captor.getValue().getCode() );
    assertEquals( "A Message", captor.getValue().getMessage() );
  }

  @Test
  public void testDelegatesPositionErrorWhenWatch() {
    GeolocationListener listener = mock( GeolocationListener.class );
    GeolocationImpl geolocation = new GeolocationImpl();
    geolocation.addGeolocationListener( listener );
    geolocation.watchPosition( new GeolocationOptions() );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "errorCode", "UNKNOWN" );
    properties.put( "errorMessage", "A Message" );

    Fixture.dispatchNotify( geolocation.getRemoteObject(), "LocationUpdateError", properties );

    ArgumentCaptor<PositionError> captor = ArgumentCaptor.forClass( PositionError.class );
    verify( listener ).errorReceived( captor.capture() );
    assertEquals( PositionErrorCode.UNKNOWN, captor.getValue().getCode() );
    assertEquals( "A Message", captor.getValue().getMessage() );
  }

}
