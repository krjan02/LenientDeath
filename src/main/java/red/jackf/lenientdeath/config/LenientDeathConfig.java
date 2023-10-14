package red.jackf.lenientdeath.config;

import blue.endless.jankson.Comment;

@SuppressWarnings("unused")
public class LenientDeathConfig {
    public static ConfigHandler INSTANCE = new ConfigHandler();

    @Comment("""
            Options relating to handling this config file.""")
    public Config config = new Config();

    public static class Config {
        @Comment("""
                Should Lenient Death watch this config file for changes, and auto reload them? Useful for servers.
                Options: true, false
                Default: true""")
        public boolean enableFileWatcher = true;
    }

    @Comment("""
            When players die, any dropped items from their inventory will have a glowing outline shown through walls,
            in order to help them find and recover their items. This outline only shows to the player who died, unless
            changed in the settings.""")
    public DroppedItemGlow droppedItemGlow = new DroppedItemGlow();
    public static class DroppedItemGlow {
        @Comment("""
                Should this feature be enabled?
                Options: true, false
                Default: true""")
        public boolean enabled = true;
        @Comment("""
                Who should we show a dead player's items' outlines to?
                Options: DEAD_PLAYER, DEAD_PLAYER_AND_TEAM, EVERYONE
                Default: DEAD_PLAYER_AND_TEAM""")
        public Visibility glowVisibility = Visibility.DEAD_PLAYER_AND_TEAM;

        public enum Visibility {
            DEAD_PLAYER,
            DEAD_PLAYER_AND_TEAM,
            EVERYONE
        }
    }

    public void onLoad() {
        if (config.enableFileWatcher) ConfigChangeListener.INSTANCE.start();
        else ConfigChangeListener.INSTANCE.stop();
    }
}