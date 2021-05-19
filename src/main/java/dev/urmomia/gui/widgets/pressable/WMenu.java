package dev.urmomia.gui.widgets.pressable;

public abstract class WMenu extends WPressable {
    public boolean open;

    public WMenu(boolean open) {
        this.open = open;
    }

    @Override
    protected void onCalculateSize() {
        double pad = pad();
        double s = theme.textHeight();

        width = pad + s + pad;
        height = pad + s + pad;
    }

    @Override
    protected void onPressed(int button) {
        open = !open;
    }
}
