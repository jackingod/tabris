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

import static com.eclipsesource.tabris.test.TabrisTestUtil.mockServiceObject;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.remote.RemoteObjectImpl;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.swt.widgets.Display;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.tabris.ClientDevice;
import com.eclipsesource.tabris.ClientDevice.Capability;
import com.eclipsesource.tabris.ClientDevice.ConnectionType;
import com.eclipsesource.tabris.ClientDevice.Orientation;
import com.eclipsesource.tabris.ClientDevice.Platform;


@SuppressWarnings("restriction")
public class ClientDeviceImplTest {

  private RemoteObjectImpl serviceObject;

  @Before
  public void setUp() {
    Fixture.setUp();
    serviceObject = ( RemoteObjectImpl )mockServiceObject();
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testGetPlatformIsAndroid() {
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( Constants.USER_AGENT, "com.eclipsesource.tabris.android" );
    ClientDevice device = new ClientDeviceImpl();

    Platform platform = device.getPlatform();

    assertSame( Platform.ANDROID, platform );
  }

  @Test
  public void testGetPlatformIsIOS() {
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( Constants.USER_AGENT, "com.eclipsesource.tabris.ios" );
    ClientDevice device = new ClientDeviceImpl();

    Platform platform = device.getPlatform();

    assertSame( Platform.IOS, platform );
  }

  @Test
  public void testGetPlatformIsWebByDefault() {
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( Constants.USER_AGENT, "Mozilla/bla" );
    ClientDevice device = new ClientDeviceImpl();

    Platform platform = device.getPlatform();

    assertSame( Platform.WEB, platform );
  }

  @Test
  public void testGetLocaleReturnsNullWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Locale locale = deviceImpl.getLocale();

    assertNull( locale );
  }

  @Test
  public void testGetLocalesReturnsEmptyArrayWhenLocaleNotSet() {
    Fixture.fakeNewGetRequest();
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Locale[] locales = deviceImpl.getLocales();

    assertEquals( 0, locales.length );
  }

  @Test
  public void testGetLocaleReadsLocaleFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Locale locale = deviceImpl.getLocale();

    assertEquals( new Locale( "en-US" ), locale );
  }

  @Test
  public void testGetLocalesReadsLocalesFromRequest() {
    TestRequest request = Fixture.fakeNewGetRequest();
    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ), new Locale( "de-DE" ) );
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Locale[] locales = deviceImpl.getLocales();

    assertEquals( 2, locales.length );
    assertEquals( new Locale( "en-US" ), locales[ 0 ] );
    assertEquals( new Locale( "de-DE" ), locales[ 1 ] );
  }

  @Test
  public void testReturnsSaveLocalesCopy() {
    TestRequest request = Fixture.fakeNewGetRequest();
    request.setHeader( "Accept-Language", "anything" );
    request.setLocales( new Locale( "en-US" ) );
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    deviceImpl.getLocales()[ 0 ] = new Locale( "de-DE" );

    Locale[] locales = deviceImpl.getLocales();
    assertEquals( new Locale( "en-US" ), locales[ 0 ] );
  }

  @Test( expected = IllegalStateException.class )
  public void testGetTimezoneOffsetFailsWhenTimezoneOffsetNotSet() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    deviceImpl.getTimezoneOffset();
  }

  @Test
  public void testGetTimezoneOffset_readsTimezoneOffsetFromHandler() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );
    Map<String, Object> parameters = new HashMap<String, Object>();
    parameters.put( "timezoneOffset", new Integer( -90 ) );

    Fixture.dispatchSet( serviceObject, parameters );
    int timezoneOffset = deviceImpl.getTimezoneOffset();

    assertEquals( -90, timezoneOffset );
  }

  @Test
  public void testDetectsPortraitMode() {
    Fixture.fakeSetParameter( "w1", "bounds", new int[] { 0, 0, 200, 400 } );
    new Display();
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Orientation orientation = deviceImpl.getOrientation();

    assertSame( Orientation.PORTRAIT, orientation );
  }

  @Test
  public void testDetectsLandscapeMode() {
    Fixture.fakeSetParameter( "w1", "bounds", new int[] { 0, 0, 400, 200 } );
    new Display();
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();

    Orientation orientation = deviceImpl.getOrientation();

    assertSame( Orientation.LANDSCAPE, orientation );
  }

  @Test( expected = IllegalStateException.class )
  public void testFailsWithoutConnectionType() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );

    deviceImpl.getConnectionType();
  }

  @Test
  public void testKnowsConnectionTypeWifi() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "connectionType", "WIFI" );

    Fixture.dispatchSet( serviceObject, properties );
    ConnectionType connectionType = deviceImpl.getConnectionType();

    assertSame( ConnectionType.WIFI, connectionType );
  }

  @Test
  public void testKnowsConnectionTypeCelular() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "connectionType", "CELULAR" );

    Fixture.dispatchSet( serviceObject, properties );
    ConnectionType connectionType = deviceImpl.getConnectionType();

    assertSame( ConnectionType.CELULAR, connectionType );
  }

  @Test( expected = IllegalStateException.class )
  public void testFailsWithoutCapabilities() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );

    deviceImpl.hasCapability( Capability.LOCATION );
  }

  @Test
  public void testCanFindCapabilities() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "capabilities", new String[] { "CAMERA", "LOCATION" } );

    Fixture.dispatchSet( serviceObject, properties );

    assertTrue( deviceImpl.hasCapability( Capability.LOCATION ) );
    assertTrue( deviceImpl.hasCapability( Capability.CAMERA ) );
    assertFalse( deviceImpl.hasCapability( Capability.MAPS ) );
    assertFalse( deviceImpl.hasCapability( Capability.MESSAGE ) );
    assertFalse( deviceImpl.hasCapability( Capability.PHONE ) );
  }

  @Test
  public void testCanFindAllCapabilities() {
    ClientDeviceImpl deviceImpl = new ClientDeviceImpl();
    when( serviceObject.getHandler() ).thenReturn( deviceImpl );
    Map<String, Object> properties = new HashMap<String, Object>();
    properties.put( "capabilities", new String[] { "CAMERA", "LOCATION", "MAPS", "MESSAGE", "PHONE" } );

    Fixture.dispatchSet( serviceObject, properties );

    assertTrue( deviceImpl.hasCapability( Capability.LOCATION ) );
    assertTrue( deviceImpl.hasCapability( Capability.CAMERA ) );
    assertTrue( deviceImpl.hasCapability( Capability.MAPS ) );
    assertTrue( deviceImpl.hasCapability( Capability.MESSAGE ) );
    assertTrue( deviceImpl.hasCapability( Capability.PHONE ) );
  }
}