package idk.plugin.report;

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

public class Main extends PluginBase {

    public void onEnable() {
        saveResource("reports.yml");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("report")) {
            if (args.length > 1) {
                Player badGuy = getServer().getPlayerExact(args[0]);
                if (badGuy == null) {
                    sender.sendMessage("\u00A7cUnknown player: " + args[0]);
                    return true;
                }
                String reason = String.join(" ", args).replaceFirst(args[0] + " ", "");
                this.getServer().getLogger().notice("\u00A7c" + sender.getName() + " reported player " + badGuy.getName() + "\u00A77: " + reason);
                getServer().getLogger().notice("\u00A7c" + sender.getName() + " reported player " + badGuy.getName() + "\u00A77: " + reason);
                sender.sendMessage("\u00A7aSuccessfully reported player \u00A7c" + badGuy.getName());
                for (Player p : this.getServer().getOnlinePlayers().values()) {
                    if (p.hasPermission("report.see")) p.sendMessage("\u00A7c" + sender.getName() + " reported player " + badGuy.getName() + "\u00A77: " + reason);
                }
                Config archive = new Config(getDataFolder() + "/reports.yml", Config.YAML);
                int id = archive.getSections("reports").size() + 1;
                archive.set("reports." + id + ".date", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));
                archive.set("reports." + id + ".reporter", sender.getName());
                archive.set("reports." + id + ".reported", badGuy.getName());
                archive.set("reports." + id + ".reason", reason);
                archive.save();
                return true;
            }
            return false;
        } else if (sender instanceof Player && cmd.getName().equalsIgnoreCase("reports")) {
            Player p = (Player) sender;
            if (!p.hasPermission("report.see")) return true;
            Config archive = new Config(getDataFolder() + "/reports.yml", Config.YAML);
            StringJoiner sj = new StringJoiner("\n\n\n");
            String reports = "No any reports";
            if (archive.getSections("reports").size() > 0) {
                archive.getSections("reports").forEach((s, o) -> {
                    sj.add("ID: " + s + "\nDate: " + ((ConfigSection) o).getString("date") + "\nReporter: " + ((ConfigSection) o).getString("reporter") + "\nReported: " + ((ConfigSection) o).getString("reported") + "\nReason: " + ((ConfigSection) o).getString("reason"));
                });
                reports = sj.toString();
            }
            p.showFormWindow(new FormWindowSimple("Reports", reports));
            return true;
        }
        return true;
    }
}
