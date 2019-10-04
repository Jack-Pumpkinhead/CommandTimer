package land.oz.munchkin.repeater;

import land.oz.munchkin.Home;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.file.YamlConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by CowardlyLion on 2019/10/2 20:44
 */
public class RepeatTask extends BukkitRunnable {
    List<String> commands;

    Location detection;

    Location information;

    public List<String> getCommands() {
        return commands;
    }

    public Location getDetection() {
        return detection;
    }

    public Location getInformation() {
        return information;
    }

    public static RepeatTask loadTask(String yml) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(Home.myPlugin.getDataFolder(), yml));
        List<String> commands = config.getStringList("Commands");
        Iterator<String> iterator = commands.iterator();
        while (iterator.hasNext()) {
            String command = iterator.next();
            String[] s = command.split("\\s+");
            if (s.length == 0 || s[0].equals("repeatcommand")) {
                iterator.remove();
            }
        }
        RepeatTask repeatTask = new RepeatTask(commands);

        ConfigurationSection detect = config.getConfigurationSection("detect");
        if (detect != null) {
            String worldName = detect.getString("world");
            World world = worldName != null ? Bukkit.getWorld(worldName) : Bukkit.getWorlds().get(0);
            if (world != null) {
                repeatTask.setDetection(new Location(
                        world, detect.getInt("x"), detect.getInt("y"), detect.getInt("z")));
            }
        }

        ConfigurationSection info = config.getConfigurationSection("info");
        if (info != null) {
            String worldName = info.getString("world");
            World world = worldName != null ? Bukkit.getWorld(worldName) : Bukkit.getWorlds().get(0);
            repeatTask.setDetection(new Location(
                    world, info.getInt("x"), info.getInt("y"), info.getInt("z")));
        }

        return repeatTask;
    }

    public void save(String name) throws IOException {
        YamlConfiguration config = new YamlConfiguration();
        config.set("Commands", commands);
        if (detection != null) {
            ConfigurationSection detect = config.createSection("detect");
            World world = detection.getWorld();
            detect.set("world", world != null ? world.getName() : Bukkit.getWorlds().get(0));
            detect.set("x", detection.getBlockX());
            detect.set("y", detection.getBlockY());
            detect.set("z", detection.getBlockZ());
        }
        if (information != null) {
            ConfigurationSection detect = config.createSection("info");
            World world = information.getWorld();
            detect.set("world", world != null ? world.getName() : Bukkit.getWorlds().get(0));
            detect.set("x", information.getBlockX());
            detect.set("y", information.getBlockY());
            detect.set("z", information.getBlockZ());
        }
        config.save(new File(Home.myPlugin.getDataFolder(), name));
    }


    public RepeatTask(@NotNull List<String> commands) {
        this.commands = commands;
    }

    public RepeatTask(@NotNull List<String> commands, Location detection, Location information) {
        this.commands = commands;
        this.detection = detection;
        this.information = information;
    }

    public void setDetection(Location detection) {
        this.detection = detection;
    }

    public void setInformation(Location information) {
        this.information = information;
    }


    @Override
    public void run() {
        if (detection != null && !detection.getBlock().isBlockPowered()) {
            return;
        }

        for (String command : commands) {
            /*Player abc = Bukkit.getPlayer("abc");
            if (abc != null) {
                abc.sendMessage("running " + command);
            }*/
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    public String getShortInfo() {
        if (commands.size() == 0) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (String c : commands) {
            builder.append(c, 0, Math.min(c.length(), 10)).append("... ");
        }
        return builder.toString();
    }

    public String getShortInfo(int length) {
        if (commands.size() == 0) {
            return "null";
        }
        StringBuilder builder = new StringBuilder();
        for (String c : commands) {
            builder.append(c.substring(0, Math.min(c.length(), length))).append("... ");
        }
        return builder.toString();
    }

    public String getFullInfo() {
        StringBuilder builder = new StringBuilder();

        if (detection != null) {
            builder.append("Detection:[" + "world=").append(Objects.requireNonNull(detection.getWorld()).getName())
                    .append(", x=").append(detection.getBlockX())
                    .append(", y=").append(detection.getBlockY())
                    .append(", z=").append(detection.getBlockZ()).append("]");
        }

        if (information != null) {
            builder.append(" Detection:[" + "world=").append(Objects.requireNonNull(information.getWorld()).getName())
                    .append(", x=").append(information.getBlockX())
                    .append(", y=").append(information.getBlockY())
                    .append(", z=").append(information.getBlockZ()).append("] ");

        }
        builder.append("Commands:");
        for (String c : commands) {
            builder.append("[").append(c).append("]");
        }
        return builder.toString();
    }
}
