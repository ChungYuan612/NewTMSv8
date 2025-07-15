package me.cyperion.ntms.Command;

import me.cyperion.ntms.NewTMSv8;
import me.cyperion.ntms.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;

import static me.cyperion.ntms.Utils.colors;

/**
 * 每次簽到指令/signin<br>
 * 關聯：NewTMSv8 註冊
 */
public class SigninCommand implements CommandExecutor {

    private NewTMSv8 plugin;

    public static final LinkedList<UUID> signinedList = new LinkedList<>();
    private boolean signInClose = false;
    private Random random = new Random();

    public SigninCommand(NewTMSv8 plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player player))
            return true;

        if(args.length == 1 && player.isOp()){
            if(args[0].equalsIgnoreCase("list")){
                player.sendMessage(colors("&6[簽到列表]"));
                for(UUID uuid : signinedList){
                    Player target = Bukkit.getPlayer(uuid);
                    if(target != null)
                        player.sendMessage(colors("&b"+target.getName()));
                }
                player.sendMessage(colors("&f共有 &a"+signinedList.size()+" &f人簽到"));
                return true;
            }
            signInClose = !signInClose;
            if(signInClose){
                player.sendMessage(colors("&6[提示] &c簽到已關閉"));
            }else{
                player.sendMessage(colors("&6[提示] &a簽到已開放"));
            }
            return true;
        }
        if(signInClose){
            player.sendMessage(colors("&6[提示] &c簽到暫不開放"));
            return true;
        }
        if(plugin.UNDER_MAINTENANCE){
            player.sendMessage(colors("&c伺服器正在下線維修"));
            return true;
        }
        if(signinedList.contains(player.getUniqueId())){
            player.sendMessage(colors("&6[錯誤] &c您已經簽到過了喔"));
            return true;
        }
        //簽到成功
        int count = plugin.getPlayerData(player).getSignInCount();
        int playerCount = Bukkit.getOnlinePlayers().size()-1;
        int playerBonus = playerCount * 100;
        int playerBase = Utils.curveGrowth(count);
        int money = 1500 + playerBase +  playerBonus;
        //+ plugin.getPlayerData(player).getSignInCount()*10
        plugin.getEconomy().depositPlayer(player,money);
        plugin.getPlayerData(player).addSignInCount(1);//未來可以拿這個來做新功能 TODO
        player.sendMessage(colors("&a因為現在其他在線人數為&6"+playerCount+"&a人,所以你額外獲得&6"+playerBonus+"&a元"));
        player.sendMessage(colors("&6[提示] &a成功簽到，本次簽到獲得&61500+"+playerBase+"+"+playerBonus+"="+money+"&a元! 總共累積簽到 &3"+count+" &a次!"));
        Bukkit.broadcastMessage(colors(
                "&6[公告] &b"+player.getName()+"&a 成功簽到！共簽到&3"+count+"&a次，還沒簽到的趕快簽到喔，輸入&3/signin&a簽到。")
        );
        for(Player p:Bukkit.getOnlinePlayers()){
            if(p.equals(player)){
                continue;
            }
            int bonus = random.nextInt(5,count*5);
            p.sendMessage(colors("&6[紅包] &b"+player.getName()+"&3簽到成功，發放給你&6"+bonus+"&3元紅包。"));
            player.sendMessage(colors("&6[紅包] &3你發給&b"+p.getName()+" &6"+bonus+"&3元紅包。"));
            plugin.getLogger().info(p.getName()+"獲得"+bonus+"元紅包");
            plugin.getEconomy().depositPlayer(p,bonus);
        }
        signinedList.add(player.getUniqueId());

        return true;
    }

    /**
     * 這裡先放靜態資料，和59~61行對應
     */
    public static int countSigninMoney(int signinCount){
        return 1000+Utils.curveGrowth(signinCount)+(Bukkit.getOnlinePlayers().size()-1)*100;
    }

    public static boolean isSigned(Player player){
        return signinedList.contains(player.getUniqueId());
    }
}
