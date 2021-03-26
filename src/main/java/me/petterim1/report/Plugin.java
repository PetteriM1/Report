package me.petterim1.report;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;

import java.util.Date;
import java.util.StringJoiner;
import java.text.SimpleDateFormat;

public class Plugin extends PluginBase {

    private boolean discordChatConsoleIntegration;
    private Config archive;

    private String msg_unknownplayer;
    private String msg_cantreport;
    private String msg_reportedsuccessfully;
    private String msg_whoreported;
    private String msg_noreports;
    private String msg_reports;

    public void onEnable() {
        saveDefaultConfig();
        discordChatConsoleIntegration = getConfig().getBoolean("discordChatConsoleIntegration");
        if (discordChatConsoleIntegration) {
            try {
                Class.forName("me.petterim1.discordchat.API");
            } catch (Exception ignore) {
                discordChatConsoleIntegration = false;
                getLogger().info("DiscordChat not found, disabling integration");
            }
        }
        msg_unknownplayer = getConfig().getString("msg_unknownplayer", "msg_unknownplayer");
        msg_cantreport = getConfig().getString("msg_cantreport", "msg_cantreport");
        msg_reportedsuccessfully = getConfig().getString("msg_reportedsuccessfully", "msg_reportedsuccessfully");
        msg_whoreported = getConfig().getString("msg_whoreported", "msg_whoreported");
        msg_noreports = getConfig().getString("msg_noreports", "msg_noreports");
        msg_reports = getConfig().getString("msg_reports", "msg_reports");
        saveResource("reports.yml");
        archive = new Config(getDataFolder() + "/reports.yml", Config.YAML);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            if (cmd.getName().equalsIgnoreCase("report")) {
                if (args.length > 1) {
                    Player badGuy = getServer().getPlayerExact(args[0]);
                    if (badGuy == null) {
                        sender.sendMessage(msg_unknownplayer + args[0]);
                        return true;
                    }
                    if (badGuy.equals(sender)) {
                        sender.sendMessage(msg_cantreport);
                        return true;
                    }
                    String reason = String.join(" ", args).replaceFirst(args[0] + " ", "");
                    int id = archive.getSections("reports").size() + 1;
                    archive.set("reports." + id + ".date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                    archive.set("reports." + id + ".reporter", sender.getName());
                    archive.set("reports." + id + ".reported", badGuy.getName());
                    archive.set("reports." + id + ".reason", reason);
                    archive.save(true);
                    getServer().getLogger().notice("\u00A7c" + sender.getName() + msg_whoreported + badGuy.getName() + "\u00A77: " + reason);
                    for (Player p : getServer().getOnlinePlayers().values()) {
                        if (p.hasPermission("report.see"))
                            p.sendMessage("\u00A7c" + sender.getName() + msg_whoreported + badGuy.getName() + "\u00A77: " + reason);
                    }
                    if (discordChatConsoleIntegration) {
                        me.petterim1.discordchat.API.sendToConsole("[Report] " + sender.getName() + msg_whoreported + badGuy.getName() + ": " + reason);
                    }
                    sender.sendMessage(msg_reportedsuccessfully + badGuy.getName());
                    return true;
                }
                return false;
            } else if (cmd.getName().equalsIgnoreCase("reports")) {
                Player p = (Player) sender;
                if (!p.hasPermission("report.see")) return true;
                StringJoiner sj = new StringJoiner("\n\n\n");
                String reports = msg_noreports;
                if (archive.getSections("reports").size() > 0) {
                    archive.getSections("reports").forEach((s, o) -> {
                        sj.add("ID: " + s + "\nDate: " + ((ConfigSection) o).getString("date") + "\nReporter: " + ((ConfigSection) o).getString("reporter") + "\nReported: " + ((ConfigSection) o).getString("reported") + "\nReason: " + ((ConfigSection) o).getString("reason"));
                    });
                    reports = sj.toString();
                }
                p.showFormWindow(new FormWindowSimple(msg_reports, reports));
                return true;
            }
        } else {
            sender.sendMessage("This command can be used only as a player.");
        }
        return true;
    }
}
