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
package com.eclipsesource.tabris.internal.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.internal.application.ApplicationContext;
import org.eclipse.rap.rwt.internal.service.ContextProvider;
import org.eclipse.rap.rwt.internal.theme.Theme;
import org.eclipse.rap.rwt.internal.theme.ThemeManager;
import org.eclipse.rap.rwt.internal.theme.css.CssFileReader;
import org.eclipse.rap.rwt.internal.theme.css.StyleSheet;
import org.eclipse.rap.rwt.lifecycle.PhaseEvent;
import org.eclipse.rap.rwt.lifecycle.PhaseId;
import org.eclipse.rap.rwt.resources.ResourceLoader;
import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.rap.rwt.testfixture.TestRequest;
import org.eclipse.rap.rwt.testfixture.internal.engine.ThemeManagerHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.eclipsesource.tabris.Bootstrapper;
import com.eclipsesource.tabris.internal.Constants;


@SuppressWarnings("restriction")
public class ThemePhaseListenerTest {
  
  private static final String CURRENT_THEME_ID = "org.eclipse.rap.theme.current";
  private ThemePhaseListener listener;

  @Before
  public void setUp() {
    Fixture.setUp();
    Fixture.fakeNewRequest();
    listener = new ThemePhaseListener();
  }
  
  @After
  public void tearDown() {
    Fixture.tearDown();
  }
  
  
  @Test
  public void testPhaseId() {
    assertEquals( PhaseId.PREPARE_UI_ROOT, listener.getPhaseId() );
  }
  
  @Test
  public void testUsesIOSTheme() throws IOException {
    registerTheme( Bootstrapper.THEME_ID_IOS );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( Constants.USER_AGENT, Constants.ID_IOS );
    
    listener.beforePhase( mock( PhaseEvent.class ) );
    
    String currentTheme = ( String )ContextProvider.getSessionStore().getAttribute( CURRENT_THEME_ID );
    assertEquals( Bootstrapper.THEME_ID_IOS, currentTheme );
  }
  
  @Test
  public void testUsesAndroidTheme() throws IOException {
    registerTheme( Bootstrapper.THEME_ID_ANDROID );
    TestRequest request = ( TestRequest )RWT.getRequest();
    request.setHeader( Constants.USER_AGENT, Constants.ID_ANDROID );
    
    listener.beforePhase( mock( PhaseEvent.class ) );
    
    String currentTheme = ( String )ContextProvider.getSessionStore().getAttribute( CURRENT_THEME_ID );
    assertEquals( Bootstrapper.THEME_ID_ANDROID, currentTheme );
  }

  private void registerTheme( String themeId ) throws IOException {
    ResourceLoader resourceLoader = new ResourceLoader() {
      
      public InputStream getResourceAsStream( String resourceName ) throws IOException {
        return new ByteArrayInputStream( "".getBytes() );
      }
    };
    StyleSheet styleSheet = CssFileReader.readStyleSheet( "", resourceLoader );
    Theme theme = new Theme( themeId, "unknown", styleSheet );
    ApplicationContext applicationContext = ContextProvider.getContext().getApplicationContext();
    ThemeManagerHelper.resetThemeManager();
    ThemeManager themeManager = applicationContext.getThemeManager();
    themeManager.registerTheme( theme );
  }
}
