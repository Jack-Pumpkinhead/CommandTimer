package land.oz.munchkin.command;

import land.oz.munchkin.Home;
import land.oz.munchkin.repeater.RepeatTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CowardlyLion on 2019/10/2 20:08
 */
public class MainCommand implements TabExecutor {

    public static final String load = "load";

    public static final String list = "list";
    public static final String run = "run";
    public static final String delete = "delete";


    public static final String listRunning = "listRunning";
    public static final String cancel = "cancel";

    public static final String save = "save";
    public static final String addOneCommand = "addOneCommand";
    public static final String detect = "detect";
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("help");
            return true;
        }

        switch (args[0]) {
            case load: {
                if (args.length < 2) {
                    return false;
                }
                String file = args[1];
                RepeatTask task = RepeatTask.loadTask(file);
                String name = args.length >= 3 ? args[2] : file;
                Home.taskList.addTask(name, task);
                return true;
            }
            case list: {
                if (args.length > 1) {
                    RepeatTask task = Home.taskList.getTasks().get(args[1]);
                    sender.sendMessage(task.getFullInfo());
                    return true;
                }
                Map<String, RepeatTask> tasks = Home.taskList.getTasks();
                sender.sendMessage("-----List of loaded Tasks-----");
                tasks.forEach((n, t) -> {
                    sender.sendMessage(n + "||" + t.getShortInfo());
                });
                sender.sendMessage("------------------------------");
                return true;
            }
            case run: {
                if (args.length < 5) {
                    return false;
                }
                String name = args[1];
                long delay = Long.parseLong(args[2]);
                if (delay < 0) {
                    sender.sendMessage("Wrong input.");
                }
                long period = Long.parseLong(args[3]);
                if (period < 0) {
                    sender.sendMessage("Wrong input.");
                }
                if (period == 0) {
                    period = 1;
                }
                String runningName = args[4];
                Home.taskList.run(name, delay, period, runningName, sender);
                return true;
            }

            case delete:{
                if (args.length < 2) {
                    return false;
                }
                Home.taskList.deleteTask(args[1]);
                return true;
            }
            case listRunning:{
                sender.sendMessage("-----List of running Tasks-----");
                Home.taskList.getRunning().forEach((n, t) -> {
                    sender.sendMessage(n + "||" + t.getShortInfo());
                });
                sender.sendMessage("------------------------------");
                return true;
            }
            case cancel:{
                if (args.length < 2) {
                    return false;
                }
                Home.taskList.cancel(args[1]);
                return true;
            }
            case save:{
                if (args.length < 2) {
                    return false;
                }
                String name = args[1];
                RepeatTask task = Home.taskList.getTasks().get(name);
                if (task != null) {
                    try {
                        task.save(name);
                    } catch (IOException e) {
                        sender.sendMessage("Saving error.");
                        e.printStackTrace();
                    }
                }
                return true;
            }
            case addOneCommand:{
                if (args.length < 3) {
                    return false;
                }
                String name = args[1];
                if (args[2].equals("repeatcommand")) {
                    sender.sendMessage("Could not repeat 'repeatcommand' !");
                    return false;
                }

                RepeatTask task = Home.taskList.getTasks().get(name);
                if (task == null) {
                    task = new RepeatTask(new ArrayList<>());
                }

                StringBuilder c = new StringBuilder(args[2]);
                for (int i = 3; i < args.length; i++) {
                    c.append(" ").append(args[i]);
                }
                task.getCommands().add(c.toString());
                Home.taskList.addTask(name, task);
                return true;
            }
            case detect:{
                if (args.length < 3) {
                    return false;
                }
                String name = args[1];
                RepeatTask task = Home.taskList.getTasks().get(name);
                if (task == null) {
                    return false;
                }
                String arg = args[2];

                switch (arg) {
                    case "set":{

                        if (args.length < 6) {
                            if (sender instanceof Entity) {
                                Entity s = (Entity) sender;
                                task.setDetection(new Location(
                                        s.getWorld(), s.getLocation().getX(), s.getLocation().getY(), s.getLocation().getZ()));
                                break;
                            } else {
                                return false;
                            }
                        } else {
                            World world = args.length >= 7 ? Bukkit.getWorld(args[6]) : Bukkit.getWorlds().get(0);
                            if(world==null) return false;

                            task.setDetection(new Location(world, Long.parseLong(args[3]), Long.parseLong(args[4]), Long.parseLong(args[5])));
                            break;
                        }
                    }
                    case "disable":{
                        task.setDetection(null);
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    List<String> commandList1 = Arrays.asList(load, list, run, delete, listRunning, cancel, save, addOneCommand, detect);


    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            return commandList1.stream().filter(s -> StringUtil.startsWithIgnoreCase(s, args[0])).collect(Collectors.toList());
        }

        switch (args[0]) {
            case load: {
                if (args.length == 2) {
                    File[] files = Home.myPlugin.getDataFolder().listFiles(File::isFile);
                    if (files == null) {
                        ArrayList<String> result = new ArrayList<>();
                        result.add("(no_file_found)");
                        return result;
                    }
                    return Arrays.stream(files).map(File::getName)
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
            }
            case list: {
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
            }
            case run: {
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                if (args.length == 3) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("0");
                    return result;
                }
                if (args.length == 4) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("1");
                    return result;
                }
                if (args.length == 5) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("runningName");
                    return result;
                }
                break;
            }
            case delete:{
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                break;
            }
            case listRunning:{
                return null;
            }
            case cancel:{
                if (args.length == 2) {
                    return Home.taskList.getRunning().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                break;
            }
            case save:{
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                break;
            }
            case addOneCommand:{
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                if (args.length == 3) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("kill");
                    return result;
                }
                break;
            }
            case detect: {
                if (args.length == 2) {
                    return Home.taskList.getTasks().keySet().stream()
                            .filter(s -> StringUtil.startsWithIgnoreCase(s, args[1]))
                            .sorted(String.CASE_INSENSITIVE_ORDER)
                            .collect(Collectors.toList());
                }
                if (args.length == 3) {
                    ArrayList<String> result = new ArrayList<>();
                    result.add("set");
                    result.add("disable");
                    return result;
                }
                if (args[2].equals("set") && sender instanceof Player) {
                    Player player = (Player) sender;
                    if (args.length == 4) {
                        ArrayList<String> result = new ArrayList<>();
                        result.add(String.valueOf(player.getLocation().getBlockX()));
                        return result;
                    }
                    if (args.length == 5) {
                        ArrayList<String> result = new ArrayList<>();
                        result.add(String.valueOf(player.getLocation().getBlockY()));
                        return result;
                    }
                    if (args.length == 6) {
                        ArrayList<String> result = new ArrayList<>();
                        result.add(String.valueOf(player.getLocation().getBlockZ()));
                        return result;
                    }
                    if (args.length == 7) {
                        ArrayList<String> result = new ArrayList<>();
                        result.add(String.valueOf(player.getWorld().getName()));
                        return result;
                    }
                    break;
                }
            }
        }
        return null;
    }
}
