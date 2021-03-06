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
package com.eclipsesource.tabris.widgets.enhancement;

import static com.eclipsesource.tabris.internal.DataWhitelist.WhiteListEntry.ALT_SELECTION;
import static com.eclipsesource.tabris.internal.DataWhitelist.WhiteListEntry.BACK_FOCUS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import org.eclipse.rap.rwt.testfixture.Fixture;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.eclipsesource.tabris.widgets.enhancement.TreeDecorator.TreePart;

@RunWith( MockitoJUnitRunner.class )
public class TreeDecoratorTest {

  @Mock
  private Tree tree;
  private TreeDecorator decorator;
  private Display display;


  @Before
  public void setUp() {
    Fixture.setUp();
    display = new Display();
    decorator = Widgets.onTree( tree );
  }

  @After
  public void tearDown() {
    Fixture.tearDown();
  }

  @Test
  public void testUseTitle() {
    decorator.useTitle( "test" );

    verify( tree ).setToolTipText( eq( "test" ) );
  }

  @Test
  public void testSetAlternativeLeafSelection() {
    decorator.enableAlternativeSelection( TreePart.LEAF );

    verify( tree ).setData( ALT_SELECTION.getKey(), "leaf" );
  }

  @Test
  public void testSetAlternativeBranchSelection() {
    decorator.enableAlternativeSelection( TreePart.BRANCH );

    verify( tree ).setData( ALT_SELECTION.getKey(), "branch" );
  }

  @Test
  public void testSetAlternativeSelectionForAll() {
    decorator.enableAlternativeSelection( TreePart.ALL );

    verify( tree ).setData( ALT_SELECTION.getKey(), "all" );
  }

  @Test
  public void testSetBackButtonFocusShouldSetBackFocusVariant() {
    Shell shell = new Shell( display );
    Tree focusTree = new Tree( shell, SWT.NONE );

    Widgets.onTree( focusTree ).enableBackButtonNavigation();

    assertEquals( Boolean.TRUE, focusTree.getData( BACK_FOCUS.getKey() ) );
  }

  @Test
  public void testSetBackButtonFocusShouldSetNullVariantOnOtherTreesWithBackFocusVariant() {
    Shell shell = new Shell( display );
    Tree tree1 = new Tree( shell, SWT.NONE );
    Tree tree2 = new Tree( shell, SWT.NONE );
    Tree tree3 = new Tree( shell, SWT.NONE );
    tree2.setData( BACK_FOCUS.getKey(), Boolean.TRUE );
    tree3.setData( BACK_FOCUS.getKey(), "anyVariant" );

    Widgets.onTree( tree1 ).enableBackButtonNavigation();

    assertEquals( Boolean.TRUE, tree1.getData( BACK_FOCUS.getKey() ) );
    assertNull( tree2.getData( BACK_FOCUS.getKey() ) );
    assertEquals( "anyVariant", tree3.getData( BACK_FOCUS.getKey() ) );
  }

}
