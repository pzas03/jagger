package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.treegrid.TreeGrid;


public class UserCommentBox extends DialogBox {

    private int maxlength = 250;
    private int height = 600;
    private int width = 400;

    private final int PIXELS_BETWEEN_BUTTONS = 10;

    private VerticalPanel vp;
    private TextButton saveButton;
    private TextButton cancelButton;
    private TextArea textArea;

    private TreeGrid<SessionComparisonPanel.TreeItem> treeGrid;

    private Label remainingCharsLabel;

    public UserCommentBox(int maxlength) {

        this.maxlength = maxlength;
        addStyleName(JaggerResources.INSTANCE.css().userCommentBox());

        setTitle("User Comment");
        vp = new VerticalPanel();
        textArea = new TextArea();
        textArea.setPixelSize(height, width);
        textArea.getElement().setAttribute("maxlength", String.valueOf(maxlength));
        setGlassEnabled(true);
        setPopupPosition(300, 100);

        HorizontalPanel remainCharsPanel = new HorizontalPanel();
        remainCharsPanel.setWidth("100%");
        remainCharsPanel.setHorizontalAlignment(HasAlignment.ALIGN_LEFT);
        remainCharsPanel.setSpacing(5);
        remainingCharsLabel = new Label(String.valueOf(maxlength));
        remainingCharsLabel.setHorizontalAlignment(HasAlignment.ALIGN_RIGHT);
        remainCharsPanel.add(remainingCharsLabel);
        remainCharsPanel.add(new HTML(" characters left."));

        textArea.addKeyPressHandler(new KeyPressHandler()
        {
            @Override
            public void onKeyPress(KeyPressEvent event){
                onTextAreaContentChanged();
            }

        });
        textArea.addKeyDownHandler(new KeyDownHandler() {
            @Override
            public void onKeyDown(KeyDownEvent event) {
                onTextAreaContentChanged();
            }
        });
        textArea.addKeyUpHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                onTextAreaContentChanged();
            }
        });


        saveButton = new TextButton("Save");
        saveButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                // ask some service to save comment
                currentTreeItem.put(getText(), textArea.getText());
                treeGrid.getTreeView().refresh(false);
                hide();
            }
        });
        saveButton.setPixelSize(60, 22);
        saveButton.getElement().setMargins(new Margins(0, 0, 0, 0));
        cancelButton = new TextButton("Cancel");
        cancelButton.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                // ask some service to save comment
                hide();
            }
        });
        cancelButton.setPixelSize(60, 22);
        cancelButton.getElement().setMargins(new Margins(0, 0, 0, PIXELS_BETWEEN_BUTTONS));
        vp.add(textArea);
        DockPanel dp = new DockPanel();
        dp.setWidth("100%");
        dp.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
        dp.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setSpacing(5);
        hp.add(saveButton);
        hp.add(cancelButton);

        dp.add(hp, DockPanel.EAST);
        dp.add(new Label(""), DockPanel.CENTER);
        dp.add(remainCharsPanel, DockPanel.WEST);
        vp.add(dp);
        setAutoHideEnabled(true);
        add(vp);
    }

    public void setTreeGrid(TreeGrid<SessionComparisonPanel.TreeItem> treeGrid) {
        this.treeGrid = treeGrid;
    }

    private void onTextAreaContentChanged() {
        int counter = textArea.getText().length();

        if (GXT.isChrome()) {
            for (char c : textArea.getText().toCharArray()) {
                if (c == '\n') {
                    counter ++;
                }
            }
            if (counter > maxlength)
                counter = maxlength;
        }

        int charsRemaining = maxlength - counter;
        remainingCharsLabel.setText("" + charsRemaining);
        if (charsRemaining >= 0)
        {
            remainingCharsLabel.setStyleName("");
        } else
            remainingCharsLabel.setStyleName("");
    }

    private SessionComparisonPanel.TreeItem currentTreeItem;

    public void popUp(String sessionId, String userComment, SessionComparisonPanel.TreeItem item) {
        currentTreeItem = item;
        setText(sessionId);
        textArea.setText(userComment);
        onTextAreaContentChanged();
        show();
        textArea.setFocus(true);
    }
}
