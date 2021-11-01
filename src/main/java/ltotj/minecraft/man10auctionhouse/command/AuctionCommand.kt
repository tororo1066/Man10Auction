package ltotj.minecraft.man10auctionhouse.command

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandManager
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandObject
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class AuctionCommand: CommandManager(Main.plugin,"mah", Main.pluginTitle) {

    init {
        setPermission("man10auction.user")
        setFunction(consumer = {
            if(it !is Player){
                it.sendMessage("${Main.pluginTitle}§4はプレイヤーのみが実行出来ます")
                return@setFunction
            }
            Main.mainGUI.open(it)
        })
    }

}