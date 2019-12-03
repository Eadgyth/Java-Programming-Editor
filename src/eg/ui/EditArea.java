package eg.ui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Font;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JTextPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JScrollBar;
import javax.swing.KeyStroke;

import javax.swing.border.LineBorder;

import javax.swing.text.DefaultCaret;

//--Eadgyth--//
import eg.BackgroundTheme;
import eg.utils.ScreenParams;

/**
 * Defines the panel that contains the area for editing text and the
 * area that displays line numbers.
 * <p>
 * The pre-defined shortcuts for cut, copy, paste and select actions are
 * disabled in the text area
 */
public final class EditArea {

   private final static BackgroundTheme THEME = BackgroundTheme.givenTheme();

   private final static LineBorder AREA_BORDER
         = new LineBorder(THEME.background(), 5);

   private final JPanel content = UIComponents.grayBorderedPanel();
   private final JTextPane textArea = new JTextPane();
   private final JTextPane lineNrArea = new JTextPane();
   private final JPanel nonWordwrapPnl = new JPanel(new BorderLayout());
   private final JScrollPane nonWordwrapScroll = UIComponents.scrollPane();
   private final JScrollPane wordwrapScroll = UIComponents.scrollPane();
   private final JScrollPane lineNrScroll = new JScrollPane(
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

   private boolean isWordwrap;

   /**
    * @param wordwrap  true to enable, false to disable wordwrap
    * @param lineNumbers  true to show, false to hide line numbers.
    * Effectless if wordwrap is true.
    * @param font  the font name
    * @param fontSize  the font size
    */
   public EditArea(boolean wordwrap, boolean lineNumbers,
         String font, int fontSize) {

      this.isWordwrap = wordwrap;
      removeShortCuts();
      content.setLayout(new BorderLayout());
      initTextArea();
      initLineNrArea();
      initLineNrScrollPane();
      nonWordwrapScroll.setViewportView(nonWordwrapPnl);
      setFont(font, fontSize);
      if (wordwrap) {
         enableWordwrap();
      }
      else {
         disableWordwrap(lineNumbers);
      }
      textArea.addFocusListener(new FocusAdapter() {

         @Override
         public void focusLost(FocusEvent e) {
            textArea.getCaret().setSelectionVisible(true);
         }
      });
   }

   /**
    * Gets this <code>JPanel</code> that contains the area for editing
    * text and, if selected, the area that shows line numbers
    *
    * @return  the JPanel
    */
   public JPanel content() {
      return content;
   }

   /**
    * Gets this area for editing text
    *
    * @return  the text area
    */
   public JTextPane textArea() {
      return textArea;
   }

   /**
    * Gets this area for showing line numbers
    *
    * @return  the text area
    */
   public JTextPane lineNrArea() {
      return lineNrArea;
   }

   /**
    * Enables wordwrap and hides line numbers
    */
   public void enableWordwrap() {
      enableWordwrapImpl();
   }

   /**
    * Disables wordwrap and shows line numbers if the specified
    * boolean is true
    *
    * @param lineNumbers  true to show, false to hide line numbers
    */
   public void disableWordwrap(boolean lineNumbers) {
      showLineNumbersImpl(lineNumbers);
   }

   /**
    * Returns if wordwrap is enabled
    *
    * @return  true if enabled, false otherwise
    */
   public boolean isWordwrap() {
      return isWordwrap;
   }

   /**
    * Shows or hides line numbers
    *
    * @param b  true to show, false to hide line numnbers
    * @throws IllegalStateException  if b is true and wordwrap is
    * currently enabled
    */
   public void showLineNumbers(boolean b) {
      if (isWordwrap) {
         throw new IllegalStateException("Wordwrap is currently enabled");
      }
      showLineNumbersImpl(b);
   }

   /**
    * Sets the font
    *
    * @param font  the font name
    * @param fontSize  the font size
    */
   public void setFont(String font, int fontSize) {
      Font f = new Font(font, Font.PLAIN, ScreenParams.scaledSize(fontSize));
      lineNrArea.setFont(f);
      textArea.setFont(f);
      revalidate();
   }

   //
   //--private--/
   //

   private void showLineNumbersImpl(boolean show) {
      int pos;
      if (isWordwrap) {
         pos = wordwrapScroll.getVerticalScrollBar().getValue();
         content.remove(wordwrapScroll);
      }
      else {
         pos = nonWordwrapScroll.getVerticalScrollBar().getValue();
      }
      if (show) {
         lineNrScroll.getVerticalScrollBar().setValue(pos);
         content.add(lineNrScroll, BorderLayout.WEST);
      }
      else {
         content.remove(lineNrScroll);
      }
      nonWordwrapPnl.add(textArea, BorderLayout.CENTER);
      content.add(nonWordwrapScroll, BorderLayout.CENTER);
      isWordwrap = false;
      setScrollPos(nonWordwrapScroll, pos);
   }

   private void enableWordwrapImpl() {
      int pos = nonWordwrapScroll.getVerticalScrollBar().getValue();
      content.remove(nonWordwrapScroll);
      content.remove(lineNrScroll);
      wordwrapScroll.setViewportView(textArea);
      content.add(wordwrapScroll, BorderLayout.CENTER);
      isWordwrap = true;
      setScrollPos(wordwrapScroll, pos);
   }

   private void setScrollPos(JScrollPane scroll, int pos) {
      JScrollBar bar = scroll.getVerticalScrollBar();
      EventQueue.invokeLater(() -> {
         bar.setValue(pos);
         revalidate();
      });
   }

   private void revalidate() {
      content.revalidate();
      content.repaint();
   }

   private void initTextArea() {
      textArea.setBorder(AREA_BORDER);
      textArea.setBackground(THEME.background());
      textArea.setCaretColor(THEME.normalForeground());
      textArea.setSelectionColor(THEME.selectionBackground());
      textArea.setSelectedTextColor(THEME.normalForeground());
   }

   private void initLineNrArea() {
      lineNrArea.setBorder(AREA_BORDER);
      lineNrArea.setBackground(THEME.background());
      lineNrArea.setEditable(false);
      lineNrArea.setFocusable(false);
      DefaultCaret caret = (DefaultCaret) lineNrArea.getCaret();
      caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
   }

   private void initLineNrScrollPane() {
      lineNrScroll.setBorder(UIComponents.grayMatteBorder(0, 0, 0, 1));
      lineNrScroll.setViewportView(lineNrArea);
      lineNrScroll.getVerticalScrollBar().setModel(
            nonWordwrapScroll.getVerticalScrollBar().getModel());
   }

   private void removeShortCuts() {
      KeyStroke ksSelAll = KeyStroke.getKeyStroke("control pressed A");
      textArea.getInputMap().put(ksSelAll, "dummy");
      KeyStroke ksCut = KeyStroke.getKeyStroke("control pressed X");
      textArea.getInputMap().put(ksCut, "dummy");
      KeyStroke ksCopy = KeyStroke.getKeyStroke("control pressed C");
      textArea.getInputMap().put(ksCopy, "dummy");
      KeyStroke ksPaste = KeyStroke.getKeyStroke("control pressed V");
      textArea.getInputMap().put(ksPaste, "dummy");
   }
}
