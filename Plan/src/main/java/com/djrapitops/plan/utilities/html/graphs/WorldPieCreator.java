package main.java.com.djrapitops.plan.utilities.html.graphs;

import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.Settings;
import main.java.com.djrapitops.plan.WorldAliasSettings;
import main.java.com.djrapitops.plan.data.time.GMTimes;
import main.java.com.djrapitops.plan.data.time.WorldTimes;

import java.util.*;
import java.util.stream.Collectors;

public class WorldPieCreator {

    private WorldPieCreator() {
        throw new IllegalStateException("Utility Class");
    }

    /**
     * Used to create HighCharts series string for series and drilldown.
     *
     * @param worldTimes WorldTimes object.
     * @return String array, index 0: Series data, 1: drilldown data
     */
    public static String[] createSeriesData(WorldTimes worldTimes) {
        String[] colors = Settings.THEME_GRAPH_WORLD_PIE.toString().split(", ");
        int colLenght = colors.length;

        StringBuilder seriesBuilder = new StringBuilder("[");
        int i = 0;
        // WorldTimes Map<String, GMTimes> (GMTimes.getTotal)
        Map<String, Long> playtimePerWorld = worldTimes.getWorldTimes().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getTotal()));

        Map<String, Long> playtimePerAlias = transformToAliases(playtimePerWorld);

        List<String> worlds = new ArrayList<>(playtimePerAlias.keySet());
        Collections.sort(worlds);

        int size = playtimePerAlias.size();
        for (String alias : worlds) {
            Long value = playtimePerAlias.getOrDefault(alias, 0L);
            if (value == 0L) {
                i++;
                continue;
            }
            seriesBuilder.append("{name:'").append(alias)
                    .append("',y:").append(value)
                    .append(",color:").append(colors[i % colLenght])
                    .append(",drilldown: '").append(alias).append("'");

            seriesBuilder.append("}");
            if (i < size - 1) {
                seriesBuilder.append(",");
            }
            i++;
        }
        seriesBuilder.append("]");

        String seriesData = seriesBuilder.toString();

        String drilldownData = createDrilldownData(worldTimes);

        return new String[]{seriesData, drilldownData};
    }

    private static Map<String, Long> transformToAliases(Map<String, Long> playtimePerWorld) {
        WorldAliasSettings aliasSettings = new WorldAliasSettings(Plan.getInstance());
        Map<String, String> aliases = aliasSettings.getAliases();
        return transformToAliases(playtimePerWorld, aliases);
    }

    public static Map<String, Long> transformToAliases(Map<String, Long> playtimePerWorld, Map<String, String> aliases) {
        // TODO Optimization is possible
        WorldAliasSettings aliasSettings = new WorldAliasSettings(Plan.getInstance());

        Map<String, Long> playtimePerAlias = new HashMap<>();
        for (Map.Entry<String, Long> entry : playtimePerWorld.entrySet()) {
            String worldName = entry.getKey();
            long playtime = entry.getValue();

            if (!aliases.containsKey(worldName)) {
                aliases.put(worldName, worldName);
                aliasSettings.addWorld(worldName);
            }

            String alias = aliases.get(worldName);

            playtimePerAlias.put(alias, playtimePerAlias.getOrDefault(alias, 0L) + playtime);
        }
        return playtimePerAlias;
    }

    private static String createDrilldownData(WorldTimes worldTimes) {
        StringBuilder drilldownBuilder = new StringBuilder();
        int i = 0;

        Map<String, GMTimes> gmTimesMap = worldTimes.getWorldTimes();
        if (gmTimesMap.isEmpty()) {
            return "[]";
        }
        Map<String, GMTimes> gmTimesAliasMap = transformToGMAliases(gmTimesMap);

        int size = gmTimesMap.size();
        drilldownBuilder.append("[");
        for (Map.Entry<String, GMTimes> worldAlias : gmTimesAliasMap.entrySet()) {
            drilldownBuilder.append("{name:'").append(worldAlias.getKey())
                    .append("', id:'").append(worldAlias.getKey())
                    .append("',colors: gmPieColors,");
            drilldownBuilder.append("data: [");

            appendGMTimesForWorld(drilldownBuilder, worldAlias);

            if (i < size - 1) {
                drilldownBuilder.append(",");
            }
            i++;
        }
        drilldownBuilder.append("]");
        return drilldownBuilder.toString();
    }

    private static Map<String, GMTimes> transformToGMAliases(Map<String, GMTimes> gmTimesMap) {
        // TODO Optimization is possible
        WorldAliasSettings aliasSettings = new WorldAliasSettings(Plan.getInstance());
        Map<String, String> aliases = aliasSettings.getAliases();

        Map<String, GMTimes> gmTimesPerAlias = new HashMap<>();

        String[] gms = GMTimes.getGMKeyArray();

        for (Map.Entry<String, GMTimes> entry : gmTimesMap.entrySet()) {
            String worldName = entry.getKey();
            GMTimes gmTimes = entry.getValue();

            if (!aliases.containsKey(worldName)) {
                aliases.put(worldName, worldName);
                aliasSettings.addWorld(worldName);
            }

            String alias = aliases.get(worldName);

            GMTimes aliasGMtimes = gmTimesPerAlias.getOrDefault(alias, new GMTimes());
            for (String gm : gms) {
                aliasGMtimes.addTime(gm, gmTimes.getTime(gm));
            }
            gmTimesPerAlias.put(alias, aliasGMtimes);
        }
        return gmTimesPerAlias;
    }

    private static void appendGMTimesForWorld(StringBuilder drilldownBuilder, Map.Entry<String, GMTimes> world) {
        Map<String, Long> gmTimes = world.getValue().getTimes();
        int smallSize = gmTimes.size();
        int j = 0;
        for (Map.Entry<String, Long> entry : gmTimes.entrySet()) {
            Long time = entry.getValue();
            drilldownBuilder.append("['")
                    .append(entry.getKey())
                    .append("',")
                    .append(time)
                    .append("]");

            if (j < smallSize - 1) {
                drilldownBuilder.append(",");
            }
            j++;
        }
        drilldownBuilder.append("]}");
    }
}
