package idk.plugin.report;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase {

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (sender instanceof Player && cmd.getName().equalsIgnoreCase("report")) {
            if (args.length > 1) {
                this.getServer().getLogger().notice("\u00A7c" + sender.getName() + " reported player " + args[0] + "\u00A77: " + String.join(" ", args));
                sender.sendMessage("\u00A7aSuccessfully reported player \u00A7c" + args[0]);
                for (Player p : this.getServer().getOnlinePlayers().values()) {
                    if (p.isOp() || p.hasPermission("report.see")) p.sendMessage("\u00A7c" + sender.getName() + " reported player " + args[0] + "\u00A77: " + String.join(" ", args));
                }
                return true;
            }
            return false;
        }
        return true;
    }
}
