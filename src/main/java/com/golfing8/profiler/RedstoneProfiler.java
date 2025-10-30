package com.golfing8.profiler;

import com.golfing8.profiler.command.ProfileCommand;
import com.golfing8.profiler.listener.WorldListener;
import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

@Getter
public class RedstoneProfiler extends JavaPlugin {
    @Getter
    private static RedstoneProfiler instance;
    private WorldListener worldListener;
    private ProfilingWorld world;

    @Override
    public void onEnable() {
        instance = this;

        this.worldListener = new WorldListener();
        getServer().getPluginManager().registerEvents(this.worldListener, this);
        Objects.requireNonNull(getCommand("profile"), "Profile command not registered").setExecutor(new ProfileCommand(this));
        this.world = new ProfilingWorld("profiling");
    }
}
