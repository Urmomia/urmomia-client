package dev.urmomia.gui.themes.urmomia.widgets;

import dev.urmomia.gui.renderer.GuiRenderer;
import dev.urmomia.gui.themes.urmomia.ThemeWidget;
import dev.urmomia.gui.widgets.containers.WWindow;

public class WThemeWindow extends WWindow implements ThemeWidget {
    private boolean hh;
    public WThemeWindow(String title) {
        super(title);
    }

    @Override
    protected WHeader header() {
        return new WThemeHeader();
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (expanded || animProgress > 0) {
            renderer.quad(x, y + header.height, width, height - header.height, theme().backgroundColor.get());
            //Border
            switch(theme().borderGui.get()) {
                case None:
                hh = false;
                break;
                case Horizontal:
                renderer.horizontalGradientBoxEdges(x, y + header.height, width, height - header.height, theme().accentColor.get(), theme().accentColor2.get());
                hh = false;
                break;
                case Vertical:
                renderer.verticalGradientBoxEdges(x, y + header.height, width, height - header.height, theme().accentColor.get(), theme().accentColor2.get());
                hh = true;
                break;
                case Solid:
                renderer.qBoxEdges(x, y + header.height, width, height - header.height, theme().accentColor.get());
                hh = true;
                break;
            }
            
        }
    }

    private class WThemeHeader extends WHeader {
        @Override
        protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
            if (hh && !(theme().gradient.get())) renderer.quad(this, theme().accentColor.get());
            if (!(hh) && theme().gradient.get()) renderer.horizontalGradientQuad(this, theme().accentColor.get(), theme().accentColor2.get());
        }
    }
}
