package com.griddynamics.jagger.webclient.client.components.control;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.griddynamics.jagger.dbapi.model.LegendNode;

public class LegendNodeCell extends AbstractCell<LegendNode> {
    @Override
    public void render(Context context, LegendNode value, SafeHtmlBuilder sb) {
        sb.appendHtmlConstant(
                (value.getLine() != null ? "<font color=\'" + value.getLine().getColor() + "\'>&#9604;&#9604;</font>" : "") +
                        "<font>  " + value.getDisplayName() + "</font>");
    }

    private static LegendNodeCell cell;

    private LegendNodeCell() {}

    public static LegendNodeCell getInstance() {
        if (cell == null) {
            cell = new LegendNodeCell();
        }

        return cell;
    }
}