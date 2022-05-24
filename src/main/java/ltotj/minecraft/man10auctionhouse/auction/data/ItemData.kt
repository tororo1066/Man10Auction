package ltotj.minecraft.man10auctionhouse.auction.data

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import org.bukkit.inventory.ItemStack

open class ItemData(val id:Int, val item:ItemStack, val seller:String, val sellerCN:String, val reserve:Long, val unit:Long, var genre:Int){

    fun getIcon():GUIItem{
        return GUIItem(item)
                .addLore(arrayOf("§d一口あたりの金額：§e${unit}円","§d出品者：${sellerCN}"))
                .setNBTInt("id",id, Main.plugin)
    }

    fun getIconForOP():GUIItem{
        val icon=getIcon()
                .addLore(arrayOf("§d最低落札価格：§e${reserve}","§d出品者のMCID：§a${seller}"))
        if(genre==2){
            icon.addLore("§e§l許可済み")
        }
        return icon
    }

}