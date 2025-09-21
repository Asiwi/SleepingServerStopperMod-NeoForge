package net.madahaz.sleepingserverstoppermod;

import net.madahaz.sleepingserverstoppermod.config.SleepingServerStopperModCommonConfig;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.common.NeoForge;

import java.util.Timer;
import java.util.TimerTask;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SleepingServerStopperMod.MODID)
public class SleepingServerStopperMod {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sleepingserverstoppermod";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private int shutdownTime;
    private boolean shutdownOnLaunch;
    private static MinecraftServer server;
    private static Timer timer;

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SleepingServerStopperMod(IEventBus modEventBus, ModContainer modContainer) {

        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, SleepingServerStopperModCommonConfig.SPEC,"sleepingserverstoppermod-common.toml");
        // Register ourselves for server and other game events we are interested in
        NeoForge.EVENT_BUS.register(this);
    }


    // Server started Event.
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        onServerStart(event.getServer());
        shutdownTime = SleepingServerStopperModCommonConfig.SHUTDOWN_TIME_IN_MINUTES.get();
        shutdownOnLaunch = SleepingServerStopperModCommonConfig.SHUTDOWN_SERVER_ON_LAUNCH.get();
        LOGGER.info("[SSS] TIME = " + shutdownTime);
        LOGGER.info("[SSS] BOOL = " + shutdownOnLaunch);

        if (shutdownOnLaunch) {
            countPlayers();
        }
    }

    // Server stopping Event.
    @SubscribeEvent
    public void onServerStopping(ServerStoppingEvent event) {
        if (timer != null) {
            timer.cancel();
        }
    }

    // Player joining Event.
    @SubscribeEvent
    public void onPlayerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        onPlayerJoin();
    }

    // Player leaving Event.
    @SubscribeEvent
    public void onPlayerDisconnect(PlayerEvent.PlayerLoggedOutEvent event) {
        countPlayers();
    }

    // METHODS

    public void onServerStart(MinecraftServer server) {
        SleepingServerStopperMod.server = server;


    }

    public void countPlayers() {
        if (server.getPlayerCount() <= 1) {
            LOGGER.info(String.format("[SSS] Server Empty - Server will shutdown in %d minute(s)!", shutdownTime));
            TimerTask task = new TimerTask() {
                public void run() {
                    stopper();
                }
            };
            timer = new Timer();
            timer.schedule(task, (60000L * shutdownTime));
        }
    }

    public static void onPlayerJoin() {
        if (server.getPlayerCount() <= 1 & timer != null) {
            LOGGER.info("[SSS] Player joined - Server shutdown cancelled.");
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public static void stopper() {
        int playerCount = server.getPlayerCount();
        if (playerCount <= 0) {
            LOGGER.info("[SSS] Server empty - Server shutting down.");
            server.halt(true);
        } else {
            LOGGER.info(String.format("[SSS} Abort shutdown - %d connected player(s).", playerCount));
        }
    }
}
