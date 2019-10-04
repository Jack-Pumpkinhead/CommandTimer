package land.oz.munchkin.repeater;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

/**
 * Created by CowardlyLion on 2019/10/3 14:01
 */
public class RunningInfo {
    String taskName;
    RepeatTask task;
    BukkitTask bukkitTask;
    long delay;
    long period;

    public RunningInfo(String taskName, RepeatTask task, BukkitTask bukkitTask, long delay, long period) {
        this.taskName = taskName;
        this.task = task;
        this.bukkitTask = bukkitTask;
        this.delay = delay;
        this.period = period;
    }

    public String getTaskName() {
        return taskName;
    }

    public RepeatTask getTask() {
        return task;
    }

    public BukkitTask getBukkitTask() {
        return bukkitTask;
    }

    public long getDelay() {
        return delay;
    }

    public long getPeriod() {
        return period;
    }

    public void cancel() {
        Bukkit.getScheduler().cancelTask(bukkitTask.getTaskId());
    }

    public String getShortInfo() {
        return "TaskName:" + taskName + "," + task.getShortInfo(5) + ", delay:" + delay + ", period:" + period;
    }
}
