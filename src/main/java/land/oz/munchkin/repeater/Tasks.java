package land.oz.munchkin.repeater;

import land.oz.munchkin.Home;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

/**
 * Created by CowardlyLion on 2019/10/2 21:06
 */
public class Tasks {
    Map<String, RepeatTask> tasks = new HashMap<>();
    Map<String, RunningInfo> running = new HashMap<>();

    public Map<String, RepeatTask> getTasks() {
        return Collections.unmodifiableMap(tasks);
    }

    public Map<String, RunningInfo> getRunning() {
        return Collections.unmodifiableMap(running);
    }

    public void addTask(String name,RepeatTask task) {
        tasks.put(name, task);
    }

    /**
     * delete a task of given name and cancel corresponding running tasks.
     * @param name task name to be deleted.
     */
    public void deleteTask(String name) {
        RepeatTask task = tasks.remove(name);
        if (task != null) {
            Iterator<Map.Entry<String, RunningInfo>> iterator = running.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, RunningInfo> entry = iterator.next();
                if (entry.getValue().getTask() == task) {
                    entry.getValue().cancel();
                    iterator.remove();
                }
            }
        }
    }

    public void cancel(String name) {
        RunningInfo runningTask = running.remove(name);
        if (runningTask != null) {
            runningTask.cancel();
        }
    }


    public void run(String name, long delay, long period, String runningName, CommandSender sender) {
        RepeatTask task = tasks.get(name);
        if (task == null) {
            sender.sendMessage("Can't find task \"" + name + "\" !");
            return;
        }
        RunningInfo runningInfo = running.get(runningName);
        if (runningInfo != null) {
            runningInfo.cancel();
        }

        /*
        Player abc = Bukkit.getPlayer("abc");
        if (abc != null) {
            abc.sendMessage("running " + name);
        }*/

        running.put(runningName, new RunningInfo(name, task, task.runTaskTimer(Home.myPlugin, delay, period), delay, period));
    }
}
