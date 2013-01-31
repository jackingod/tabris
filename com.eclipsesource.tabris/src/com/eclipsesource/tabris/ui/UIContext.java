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
package com.eclipsesource.tabris.ui;

import org.eclipse.swt.widgets.Display;

import com.eclipsesource.tabris.Store;


/**
 * @since 0.11
 */
public interface UIContext {

  Display getDisplay();

  void setTitle( String title );

  PageManager getPageManager();

  ActionManager getActionManager();

  Store getGlobalStore();
}
