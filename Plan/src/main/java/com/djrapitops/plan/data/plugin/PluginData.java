package com.djrapitops.plan.data.plugin;

import com.djrapitops.plan.data.element.AnalysisContainer;
import com.djrapitops.plan.data.element.InspectContainer;
import com.djrapitops.plan.utilities.html.Html;
import com.djrapitops.plan.utilities.html.icon.Color;
import com.djrapitops.plan.utilities.html.icon.Icon;
import com.google.common.base.Objects;

import java.util.Collection;
import java.util.UUID;

/**
 * This is an abstract class that can be used to add data from a plugin to the
 * "Plugins"-sections of Analysis and Inspect pages.
 * <p>
 * API-section of documentation has examples on the usage of this class and how
 * to register objects extending this class.
 *
 * @author Rsl1122
 * @since 4.1.0
 */
public abstract class PluginData {

    private final ContainerSize size;
    private final String sourcePlugin;

    private Icon pluginIcon;
    private String iconColor;

    private String helpText;

    protected com.djrapitops.plan.data.store.containers.AnalysisContainer analysisData;

    public PluginData(ContainerSize size, String sourcePlugin) {
        this.size = size;
        this.sourcePlugin = sourcePlugin;
    }

    public abstract InspectContainer getPlayerData(UUID uuid, InspectContainer fillThis) throws Exception;

    public abstract AnalysisContainer getServerData(Collection<UUID> uuids, AnalysisContainer fillThis) throws Exception;

    protected final void setPluginIcon(Icon pluginIcon) {
        this.pluginIcon = pluginIcon;
    }

    /**
     * @deprecated Use {@code setPluginIcon(Icon)} instead
     */
    @Deprecated
    protected final void setPluginIcon(String pluginIcon) {
        this.pluginIcon = Icon.called(pluginIcon != null ? pluginIcon : "cube").build();
    }

    /**
     * @deprecated Use {@code setPluginIcon(Icon)} instead
     */
    @Deprecated
    protected final void setIconColor(String iconColor) {
        pluginIcon.setColor(Color.matchString(iconColor));
    }

    public final String getHelpText() {
        return helpText;
    }

    public final String parsePluginIcon() {
        return (pluginIcon != null ? pluginIcon : Icon.called("cube").build()).toHtml();
    }

    public final ContainerSize getSize() {
        return size;
    }

    public final String getSourcePlugin() {
        return sourcePlugin;
    }

    protected final void setHelpText(String html) {
        helpText = Html.HELP_BUBBLE.parse(sourcePlugin, html);
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PluginData that = (PluginData) o;
        return size == that.size &&
                Objects.equal(sourcePlugin, that.sourcePlugin) &&
                Objects.equal(pluginIcon, that.pluginIcon);
    }

    @Override
    public final int hashCode() {
        return Objects.hashCode(size, sourcePlugin, pluginIcon);
    }

    /**
     * @deprecated Use {@code getWithIcon(String, Icon)} instead
     */
    @Deprecated
    public final String getWithIcon(String text, String icon) {
        return getWithIcon(text, Icon.called(icon).build());
    }

    /**
     * @deprecated Use {@code getWithIcon(String, Icon)} instead
     */
    @Deprecated
    public final String getWithIcon(String text, String icon, String color) {
        return getWithIcon(text, Icon.called(icon).of(Color.matchString(color)).build());
    }

    public final String getWithIcon(String text, Icon.Builder builder) {
        return getWithIcon(text, builder.build());
    }

    public final String getWithIcon(String text, Icon icon) {
        return icon.toHtml() + " " + text;
    }

    public final void setAnalysisData(com.djrapitops.plan.data.store.containers.AnalysisContainer analysisData) {
        this.analysisData = analysisData;
    }
}
