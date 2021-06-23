package me.lucyy.pronouns.config;

import me.lucyy.squirtgun.format.FormatProvider;

import java.util.List;

public interface ConfigHandler extends FormatProvider {
    List<String> getPredefinedSets();

    List<String> getFilterPatterns();

    boolean filterEnabled();

    SqlInfoContainer getSqlConnectionData();

    boolean checkForUpdates();

    ConnectionType getConnectionType();
}
