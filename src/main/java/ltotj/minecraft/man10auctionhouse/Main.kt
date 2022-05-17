package ltotj.minecraft.man10auctionhouse

import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.menu.MainMenu
import ltotj.minecraft.man10auctionhouse.auction.system.GNAuction
import ltotj.minecraft.man10auctionhouse.auction.system.SPAuction
import ltotj.minecraft.man10auctionhouse.command.AuctionCommand
import ltotj.minecraft.man10auctionhouse.command.AuctionOPCommand
import ltotj.minecraft.man10auctionhouse.command.BiddingCommand
import org.bukkit.World
import org.bukkit.plugin.java.JavaPlugin
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Main : JavaPlugin() {

    companion object{
        const val pluginTitle="§d[§cMan10Auction§d]"
        lateinit var executor: ExecutorService
        lateinit var plugin: JavaPlugin
        lateinit var mainGUI:MainMenu
        lateinit var venue:World
        var available=true
        var inAuction=false
        var duringPublication=false
        var tax=0.05
        var auctionId=-1
        var spAuction:SPAuction?=null
        var gnAuction:GNAuction?=null
    }

    override fun onEnable() {
        plugin=this
        loadConfig()
        executor= Executors.newCachedThreadPool()
        mainGUI= AuctionFunc.createMainGUIs()
        AuctionCommand()
        AuctionOPCommand()
        BiddingCommand()
        // Plugin startup logic
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    private fun loadConfig(){
        saveDefaultConfig()
        available=config.getBoolean("available")
        auctionId=config.getInt("nextAuctionId")

        if(plugin.server.getWorld(config.getString("venue")?:"world")!=null){
            venue= plugin.server.getWorld(config.getString("venue")?:"world")!!
        }
    }

}