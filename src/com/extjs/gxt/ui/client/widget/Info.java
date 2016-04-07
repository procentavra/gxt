/*
 * Sencha GXT 2.3.1 - Sencha for GWT
 * Copyright(c) 2007-2013, Sencha, Inc.
 * licensing@sencha.com
 * 
 * http://www.sencha.com/products/gxt/license/
 */
 package com.extjs.gxt.ui.client.widget;

import java.util.ArrayList;
import java.util.Stack;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.util.Params;
import com.extjs.gxt.ui.client.util.Point;
import com.extjs.gxt.ui.client.util.Size;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Accessibility;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Displays a message in the bottom right region of the browser for a specified
 * amount of time.
 * 
 * <dl>
 * <dt>Inherited Events:</dt>
 * <dd>ContentPanel BeforeExpand</dd>
 * <dd>ContentPanel Expand</dd>
 * <dd>ContentPanel BeforeCollapse</dd>
 * <dd>ContentPanel Collapse</dd>
 * <dd>ContentPanel BeforeClose</dd>
 * <dd>ContentPanel Close</dd>
 * <dd>LayoutContainer AfterLayout</dd>
 * <dd>ScrollContainer Scroll</dd>
 * <dd>Container BeforeAdd</dd>
 * <dd>Container Add</dd>
 * <dd>Container BeforeRemove</dd>
 * <dd>Container Remove</dd>
 * <dd>BoxComponent Move</dd>
 * <dd>BoxComponent Resize</dd>
 * <dd>Component Enable</dd>
 * <dd>Component Disable</dd>
 * <dd>Component BeforeHide</dd>
 * <dd>Component Hide</dd>
 * <dd>Component BeforeShow</dd>
 * <dd>Component Show</dd>
 * <dd>Component Attach</dd>
 * <dd>Component Detach</dd>
 * <dd>Component BeforeRender</dd>
 * <dd>Component Render</dd>
 * <dd>Component BrowserEvent</dd>
 * <dd>Component BeforeStateRestore</dd>
 * <dd>Component StateRestore</dd>
 * <dd>Component BeforeStateSave</dd>
 * <dd>Component SaveState</dd>
 * </dl>
 */
public class Info extends ContentPanel {

  private static Stack<Info> infoStack = new Stack<Info>();
  private static ArrayList<Info> slots = new ArrayList<Info>();

  /**
   * Displays a message using the specified config.
   * 
   * @param config the info config
   */
  public static void display(InfoConfig config) {
    pop().show(config);
  }

  /**
   * Displays a message with the given title and text.
   * 
   * @param title the title
   * @param html the text
   */
  public static void display(SafeHtml title, SafeHtml html) {
    display(new InfoConfig(title, html));
  }

  public static void displayText(String title, String text) {
    display(new InfoConfig(SafeHtmlUtils.fromString(title), SafeHtmlUtils.fromString(text)));
  }

  private static int firstAvail() {
    int size = slots.size();
    for (int i = 0; i < size; i++) {
      if (slots.get(i) == null) {
        return i;
      }
    }
    return size;
  }

  private static Info pop() {
    Info info = infoStack.size() > 0 ? (Info) infoStack.pop() : null;
    if (info == null) {
      info = new Info();
    }
    return info;
  }

  private static void push(Info info) {
    infoStack.push(info);
  }

  protected InfoConfig config;
  protected int level;

  /**
   * Creates a new info instance.
   */
  public Info() {
    baseStyle = "x-info";
    frame = true;
    setShadow(true);
    setLayoutOnChange(true);
  }

  public void hide() {
    super.hide();
    afterHide();
  }

  /**
   * Displays the info.
   * 
   * @param config the info config
   */
  public void show(InfoConfig config) {
    this.config = config;
    onShowInfo();
  }

  protected void afterHide() {
    RootPanel.get().remove(this);
    slots.set(level, null);
    push(this);
  }
  
  protected void afterShow() {
    Timer t = new Timer() {
      public void run() {
        afterHide();
      }
    };
    t.schedule(config.display);
  }

  @Override
  protected void onRender(Element parent, int pos) {
    super.onRender(parent, pos);
    if (GXT.isAriaEnabled()) {
      Accessibility.setRole(getElement(), "alert");
    }
  }

  protected void onShowInfo() {
    RootPanel.get().add(this);
    el().makePositionable(true);

    setTitle();
    setText();
    
    level = firstAvail();
    slots.add(level, this);

    Point p = position();
    el().setLeftTop(p.x, p.y);
    setSize(config.width, config.height);
    
    afterShow();
  }

  protected Point position() {
    Size s = XDOM.getViewportSize();
    int left = s.width - config.width - 10 + XDOM.getBodyScrollLeft();
    int top = s.height - config.height - 10 - (level * (config.height + 10))
        + XDOM.getBodyScrollTop();
    return new Point(left, top);
  }

  private void setText() {
    if (config.html != null) {
      removeAll();
      addText(config.html);
    }
  }

  private void setTitle() {
    if (config.titleHtml != null) {
      head.setVisible(true);
      setHeadingHtml(config.titleHtml);
    } else {
      head.setVisible(false);
    }
  }

}
