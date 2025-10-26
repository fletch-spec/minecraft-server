package com.fletch.spawncommand;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import org.bukkit.event.player.PlayerJoinEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import java.time.Duration;


public class SpawnCommand extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) sender;
        Location spawnLocation = player.getRespawnLocation();

        if (spawnLocation == null) {
            spawnLocation = player.getWorld().getSpawnLocation();
        }

        player.teleport(spawnLocation);
        player.sendMessage("§aTeleported!");
        return true;
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        if (event.getBedEnterResult() != PlayerBedEnterEvent.BedEnterResult.OK) {
            return;
        }

        Player player = event.getPlayer();
        World world = player.getWorld();

        // Check if it's night time (time > 12541 and < 23458 approximately)
        long time = world.getTime();
        if (time >= 12541 && time <= 23458) {
            // Set to morning (time 0) and clear weather
            world.setTime(0);
            world.setStorm(false);
            world.setThundering(false);

            // Broadcast message to all players
            world.getPlayers().forEach(p ->
                    p.sendMessage("§e" + player.getName() + " slept with a villager to skip the night!")
            );
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        // Create title and subtitle
        Component title = Component.text("Settler's Era").color(NamedTextColor.DARK_GREEN);
        Component subtitle = Component.text("").color(NamedTextColor.GRAY);

        // Show title (fade in: 0.5s, stay: 3s, fade out: 1s)
        Title titleMessage = Title.title(
                title,
                subtitle,
                Title.Times.times(Duration.ofMillis(500), Duration.ofSeconds(5), Duration.ofSeconds(1))
        );

        player.showTitle(titleMessage);
        player.sendMessage("Veinminer has been added.");
    }

}

