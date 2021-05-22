package dev.urmomia.gui;

import dev.urmomia.gui.renderer.packer.GuiTexture;
import dev.urmomia.gui.screens.AccountsScreen;
import dev.urmomia.gui.screens.ModuleScreen;
import dev.urmomia.gui.screens.ModulesScreen;
import dev.urmomia.gui.screens.ProxiesScreen;
import dev.urmomia.gui.tabs.TabScreen;
import dev.urmomia.gui.utils.CharFilter;
import dev.urmomia.gui.utils.SettingsWidgetFactory;
import dev.urmomia.gui.utils.WindowConfig;
import dev.urmomia.gui.widgets.*;
import dev.urmomia.gui.widgets.containers.*;
import dev.urmomia.gui.widgets.input.*;
import dev.urmomia.gui.widgets.pressable.*;
import dev.urmomia.rendering.text.TextRenderer;
import dev.urmomia.settings.Settings;
import dev.urmomia.systems.accounts.Account;
import dev.urmomia.systems.modules.Module;
import dev.urmomia.utils.misc.ISerializable;
import dev.urmomia.utils.misc.Keybind;
import dev.urmomia.utils.misc.Names;
import dev.urmomia.utils.render.color.Color;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public abstract class GuiTheme implements ISerializable<GuiTheme> {
    public static final double TITLE_TEXT_SCALE = 1.25;

    public final String name;
    public final Settings settings = new Settings();

    public boolean disableHoverColor;

    protected SettingsWidgetFactory settingsFactory;
    
    protected final Map<String, WindowConfig> windowConfigs = new HashMap<>();

    public GuiTheme(String name) {
        this.name = name;
    }

    public void beforeRender() {
        disableHoverColor = false;
    }

    // Widgets

    public abstract WWindow window(String title);

    public abstract WLabel label(String text, boolean title, double maxWidth);
    public WLabel label(String text, boolean title) {
        return label(text, title, 0);
    }
    public WLabel label(String text, double maxWidth) {
        return label(text, false, maxWidth);
    }
    public WLabel label(String text) {
        return label(text, false);
    }

    public abstract WHorizontalSeparator horizontalSeparator(String text);
    public WHorizontalSeparator horizontalSeparator() {
        return horizontalSeparator(null);
    }
    public abstract WVerticalSeparator verticalSeparator();

    protected abstract WButton button(String text, GuiTexture texture);
    public WButton button(String text) {
        return button(text, null);
    }
    public WButton button(GuiTexture texture) {
        return button(null, texture);
    }

    public abstract WMinus minus();
    public abstract WPlus plus();

    public abstract WCheckbox checkbox(boolean checked);

    public abstract WSlider slider(double value, double min, double max);

    public abstract WTextBox textBox(String text, CharFilter filter);
    public WTextBox textBox(String text) {
        return textBox(text, (text1, c) -> true);
    }

    public abstract <T> WDropdown<T> dropdown(T[] values, T value);
    public <T extends Enum<?>> WDropdown<T> dropdown(T value) {
        Class<?> klass = value.getClass();
        T[] values = null;
        try {
            values = (T[]) klass.getDeclaredMethod("values").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return dropdown(values, value);
    }

    public abstract WTriangle triangle();

    public abstract WMenu menu(boolean open);

    public abstract WTooltip tooltip(String text);

    public abstract WView view();

    public WVerticalList verticalList() {
        return w(new WVerticalList());
    }
    public WHorizontalList horizontalList() {
        return w(new WHorizontalList());
    }
    public WTable table() {
        return w(new WTable());
    }

    public abstract WSection section(String title, boolean expanded, WWidget headerWidget);
    public WSection section(String title, boolean expanded) {
        return section(title, expanded, null);
    }
    public WSection section(String title) {
        return section(title, true);
    }

    public abstract WAccount account(WidgetScreen screen, Account<?> account);

    public abstract WWidget module(Module module);

    public abstract WQuad quad(Color color);

    public abstract WTopBar topBar();

    public WItem item(ItemStack itemStack) {
        return w(new WItem(itemStack));
    }
    public WItemWithLabel itemWithLabel(ItemStack stack, String name) {
        return w(new WItemWithLabel(stack, name));
    }
    public WItemWithLabel itemWithLabel(ItemStack stack) {
        return itemWithLabel(stack, Names.get(stack.getItem()));
    }

    public WTexture texture(double width, double height, double rotation, AbstractTexture texture) {
        return w(new WTexture(width, height, rotation, texture));
    }

    public WIntEdit intEdit(int value, int sliderMin, int sliderMax) {
        return w(new WIntEdit(value, sliderMin, sliderMax));
    }
    public WDoubleEdit doubleEdit(double value, double sliderMin, double sliderMax) {
        return w(new WDoubleEdit(value, sliderMin, sliderMax));
    }

    public WKeybind keybind(Keybind keybind) {
        return keybind(keybind, -1);
    }

    public WKeybind keybind(Keybind keybind, int defaultValue) {
        return w(new WKeybind(keybind, defaultValue));
    }

    public WWidget settings(Settings settings, String filter) {
        return settingsFactory.create(this, settings, filter);
    }
    public WWidget settings(Settings settings) {
        return settings(settings, "");
    }

    // Screens

    public TabScreen modulesScreen() {
        return new ModulesScreen(this);
    }
    public boolean isModulesScreen(Screen screen) {
        return screen instanceof ModulesScreen;
    }

    public WidgetScreen moduleScreen(Module module) {
        return new ModuleScreen(this, module);
    }

    public WidgetScreen accountsScreen() {
        return new AccountsScreen(this);
    }

    public WidgetScreen proxiesScreen() {
        return new ProxiesScreen(this);
    }
    
    // Colors

    public abstract Color textColor();

    public abstract Color textSecondaryColor();

    // Other

    public abstract TextRenderer textRenderer();

    public abstract double scale(double value);

    public abstract boolean categoryIcons();

    public abstract boolean blur();

    public double textWidth(String text, int length, boolean title) {
        return scale(textRenderer().getWidth(text, length) * (title ? TITLE_TEXT_SCALE : 1));
    }
    public double textWidth(String text) {
        return textWidth(text, text.length(), false);
    }

    public double textHeight(boolean title) {
        return scale(textRenderer().getHeight() * (title ? TITLE_TEXT_SCALE : 1));
    }
    public double textHeight() {
        return textHeight(false);
    }

    public double pad() {
        return scale(6);
    }

    public WindowConfig getWindowConfig(String id) {
        WindowConfig config = windowConfigs.get(id);
        if (config != null) return config;

        config = new WindowConfig();
        windowConfigs.put(id, config);
        return config;
    }

    public void clearWindowConfigs() {
        windowConfigs.clear();
    }

    protected <T extends WWidget> T w(T widget) {
        widget.theme = this;
        return widget;
    }

    // Saving / Loading

    @Override
    public CompoundTag toTag() {
        CompoundTag tag = new CompoundTag();

        tag.putString("name", name);
        tag.put("settings", settings.toTag());

        CompoundTag configs = new CompoundTag();
        for (String id : windowConfigs.keySet()) {
            configs.put(id, windowConfigs.get(id).toTag());
        }
        tag.put("windowConfigs", configs);

        return tag;
    }

    @Override
    public GuiTheme fromTag(CompoundTag tag) {
        settings.fromTag(tag.getCompound("settings"));

        CompoundTag configs = tag.getCompound("windowConfigs");
        for (String id : configs.getKeys()) {
            windowConfigs.put(id, new WindowConfig().fromTag(configs.getCompound(id)));
        }

        return this;
    }
}
