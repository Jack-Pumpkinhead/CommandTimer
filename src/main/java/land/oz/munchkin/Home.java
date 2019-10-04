package land.oz.munchkin;

import land.oz.munchkin.command.Commands;
import land.oz.munchkin.repeater.RepeatTask;
import land.oz.munchkin.repeater.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * Created by CowardlyLion on 2019/10/2 11:25
 */
public class Home extends JavaPlugin {
    public static JavaPlugin myPlugin;

    public static Tasks taskList;

    @Override
    public void onEnable() {
        myPlugin = this;

        try {
            getCommand("repeatcommand").setExecutor(Commands.main);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        taskList = new Tasks();



        getLogger().info("Enabled.");
    }

    @Override
    public void onDisable() {

        Bukkit.getScheduler().cancelTasks(this);

        for (Map.Entry<String, RepeatTask> entry : taskList.getTasks().entrySet()) {
            try {
                entry.getValue().save(entry.getKey());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        getLogger().info("Disabled.");

    }
}
