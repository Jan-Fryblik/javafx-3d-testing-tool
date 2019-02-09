package cz.ebrothers.fx3dtestingtool.fxviewer;

public enum ViewPoint {
	
    FRONT("-Z", "Front view"),
    BACK("+Z", "Back view"),
    LEFT("-X", "Left view"),
    RIGHT("+X", "Right view"),
    TOP("-Y", "Top view"),
    BOTTOM("+Y", "Bottom view"),
    ISO("ISO", "Isometric view"),
    CENTER("Center", "From center");
    
    private String listName;
    private String tooltip;

    private ViewPoint(String string2, String string3) {
        this.listName = string2;
        this.tooltip = string3;
    }

    String getListName() {
        return this.listName;
    }

    String getTooltip() {
        return this.tooltip;
    }
}

