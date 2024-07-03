package me.cyperion.ntms.Command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.cyperion.ntms.Utils.colors;

public class NTMSCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(args.length == 1 && args[0].equals("help")){
            sender.sendMessage(colors("&f歡迎來到臺灣地圖伺服器，接下來皆為您介紹台灣的新功能"));
            sender.sendMessage(colors("&f "));
            sender.sendMessage(colors("&5 規則介紹："));
            sender.sendMessage(colors("&f 1."));
            sender.sendMessage(colors("&f "));
            sender.sendMessage(colors("&e 指令介紹："));
            sender.sendMessage(colors("&3 /menu "));
            sender.sendMessage(colors("&f 主要的選單，基本上的功能都在裡面，可以直接點擊圖示喔!"));
            sender.sendMessage(colors("&3 /warp <rs/tw/bed>"));
            sender.sendMessage(colors("&f 傳送來往世界的指令，目前台灣有資源界可以方便玩家採集資源，透過輸入 &3/warp rs &f來傳送"));
            sender.sendMessage(colors("&3 /tpa "));
            sender.sendMessage(colors("&f 與上一季一樣有tpa功能，可以透過詢問對方來傳送到對方的位置。接受或拒絕的方法在收到tpa請求時會說明"));
            sender.sendMessage(colors("&3 /enderchest "));
            sender.sendMessage(colors("&f 隨時可以開啟終界箱而不需要帶著終界箱，方便在資源界採集資源，因為背包有限嘛~"));
            sender.sendMessage(colors("&b未來還會有更多指令加入，到時候一樣可以透過輸入 &3/ntms help &b查詢哦"));
            sender.sendMessage(colors("&f "));
            sender.sendMessage(colors("&6介紹可能不太完全，如果看完後還有疑問，歡迎到Discord社群發問喔!"));

        }
        return true;
    }
}
