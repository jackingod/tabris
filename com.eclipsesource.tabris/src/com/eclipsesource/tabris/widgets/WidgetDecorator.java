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
package com.eclipsesource.tabris.widgets;

import static com.eclipsesource.tabris.internal.WidgetsUtil.setVariant;

import org.eclipse.swt.widgets.Widget;


/**
 * @since 0.6
 */
public class WidgetDecorator<T extends WidgetDecorator> {
  
  private final Widget widget;

  WidgetDecorator( Widget widget ) {
    this.widget = widget;
  }
  
  @SuppressWarnings("unchecked")
  public T useAnimation() {
    setVariant( widget, "ANIMATED" );
    return ( T )this;
  }
  
}
