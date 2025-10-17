package com.golfing8.profiler;

import com.golfing8.profiler.listener.WorldListener;
import com.golfing8.profiler.struct.ProfilingWorld;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

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
        this.world = new ProfilingWorld("profiling");
    }
}
