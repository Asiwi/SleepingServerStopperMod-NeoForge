package net.madahaz.sleepingserverstoppermod.config;

import net.neoforged.neoforge.common.ModConfigSpec;


public class SleepingServerStopperModCommonConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final ModConfigSpec.ConfigValue<Integer> SHUTDOWN_TIME_IN_MINUTES;
    public static final ModConfigSpec.ConfigValue<Boolean> SHUTDOWN_SERVER_ON_LAUNCH;

    static {
        BUILDER.push("Configs for Sleeping Server Stopper Mod");

        SHUTDOWN_TIME_IN_MINUTES = BUILDER.comment("How many minutes until the server closes. (Minimum is 1 minute!)")
                .define("Minutes", 2);
        SHUTDOWN_SERVER_ON_LAUNCH = BUILDER.comment("Should the server shutdown on launch?")
                .define("ShutdownOnLaunch",true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
