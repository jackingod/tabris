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
package com.eclipsesource.tabris.internal.ui;

import static com.eclipsesource.tabris.internal.Preconditions.checkArgumentNotNull;

import java.util.HashMap;
import java.util.Map;

import com.eclipsesource.tabris.ui.ActionManager;


public class ActionManagerImpl implements ActionManager {

  private final Controller controller;
  private final Map<String, Boolean> visibility;
  private final Map<String, Boolean> enablement;

  public ActionManagerImpl( Controller controller ) {
    checkArgumentNotNull( controller, Controller.class.getSimpleName() );
    this.controller = controller;
    this.visibility = new HashMap<String, Boolean>();
    this.enablement = new HashMap<String, Boolean>();
  }

  @Override
  public void setActionEnabled( String id, boolean enabled ) {
    controller.setActionEnabled( id, enabled );
    enablement.put( id, Boolean.valueOf( enabled ) );
  }

  @Override
  public boolean isActionEnabled( String id ) {
    Boolean enbaled = enablement.get( id );
    if( enbaled == null || enbaled.booleanValue() ) {
      return true;
    }
    return false;
  }

  @Override
  public void setActionVisible( String id, boolean visible ) {
    controller.setActionVisible( id, visible );
    visibility.put( id, Boolean.valueOf( visible ) );
  }

  @Override
  public boolean isActionVisible( String id ) {
    Boolean visible = visibility.get( id );
    if( visible == null || visible.booleanValue() ) {
      return true;
    }
    return false;
  }
}
