package TGBUG.tgbug_edgs;

import TGBUG.tgbug_edgs.metrics.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

public final class TGBUG_EDGS extends JavaPlugin implements CommandExecutor {

    private BukkitRunnable broadcastTask;
    private Long delay;
    private String APIURL;
    private Long period;
    private List<String> message;
    private ConsoleCommandSender console;
    private boolean bStats;

    @Override
    public void onEnable() {
        loadconfig(console);
        startbroadcastTask();
        this.getCommand("everydaygoldensentence").setExecutor(this);
        if (bStats){
            Metrics metrics = new Metrics(this, 23756);
        }
        getLogger().info(ChatColor.AQUA + "TGBUG的每日金句插件已启用!");
    }

    @Override
    public void onDisable() {
        getLogger().info("TGBUG的每日金句插件已卸载!");
    }

    public void loadconfig(CommandSender sendto) {
        File configFile = new File(getDataFolder(), "config.yml");

        if (broadcastTask != null){
            broadcastTask.cancel();
            broadcastTask = null;
        }
        if (configFile.exists()) {
            reloadConfig();
        }
        else {
            saveDefaultConfig();
        }
        APIURL = getConfig().getString("api-url");
        delay = getConfig().getLong("delay") * 20;
        period = getConfig().getLong("period") * 20;
        message = getConfig().getStringList("message");
        bStats = getConfig().getBoolean("bStats");

    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (sender instanceof ConsoleCommandSender || sender.isOp()) {
            if (args.length == 0) {
                sendmessage("使用方法 /edgs [reload|view]", sender, ChatColor.GREEN);
                return true;
            }

            switch (args[0].toLowerCase()) {
                case "reload":
                    loadconfig(sender);
                    startbroadcastTask();
                    sendmessage("已重载配置文件", sender, ChatColor.GREEN);
                    return true;

                case "view":
                    broadcast();
                    return true;

                default:
                    sendmessage("未知命令", sender, ChatColor.RED);
                    return true;
            }
        }
        else {
            sendmessage("你没有权限执行此命令", sender, ChatColor.RED);
            return true;
        }
    }

    public void startbroadcastTask() {
        broadcastTask = new BukkitRunnable() {
            @Override
            public void run() {
                broadcast();
            }
        };
        broadcastTask.runTaskTimerAsynchronously(this, delay, period);
    }

    public void broadcast() {
        String APImessage = new getAPI().GetAPI(APIURL);
        //替换占位符以及颜色符号
        for (String message : message) {
            message = message.replace("%message%", APImessage);
            message = ChatColor.translateAlternateColorCodes('&', message);
            String finalMessage = message;
            Bukkit.getScheduler().runTask(TGBUG_EDGS.this, () -> Bukkit.broadcastMessage(finalMessage));
        }
    }

    public void sendmessage(String message, CommandSender sendto, ChatColor color) {
        if (sendto instanceof ConsoleCommandSender) {
            if (color == ChatColor.GREEN) {
                getLogger().info(message);
            }
            else {
                getLogger().warning(message);
            }
        } else {
            sendto.sendMessage(color + message);
        }
    }
}