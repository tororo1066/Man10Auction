package ltotj.minecraft.man10auctionhouse.command

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandManager
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandObject
import org.bukkit.plugin.java.JavaPlugin

class AuctionOPCommand : CommandManager(Main.plugin,"mahop", Main.pluginTitle) {

    init {
        setPermission("man10auction.op")

        //onコマンド
        addFirstArgument(
                CommandObject("on")
                        .setFunction(consumer = {
                            if(!Main.available) {
                                it.first.sendMessage("${Main.pluginTitle}§a再開しました")
                                Main.available=true
                                Main.plugin.config.set("available", true)
                                Main.plugin.saveConfig()
                            }
                            else{
                                it.first.sendMessage("${Main.pluginTitle}§a既に利用可能です")
                            }
                        })
        )

        //offコマンド
        addFirstArgument(
                CommandObject("off")
                        .setFunction(consumer={
                            if(Main.available) {
                                it.first.sendMessage("${Main.pluginTitle}§a停止しました")
                                Main.available=false
                                Main.plugin.config.set("available", false)
                                Main.plugin.saveConfig()
                            }
                            else{
                                it.first.sendMessage("${Main.pluginTitle}§a既に停止されています")
                            }
                        })
        )



    }


}