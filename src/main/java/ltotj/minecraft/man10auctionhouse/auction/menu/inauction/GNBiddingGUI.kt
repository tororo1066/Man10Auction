package ltotj.minecraft.man10auctionhouse.auction.menu.inauction

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.system.GNAuction
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.menu.TrueOrFalseGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class GNBiddingGUI(item: ItemStack, id:Int, price:Long, gnAuction: GNAuction): TrueOrFalseGUI(Main.plugin,"入札の最終確認"){

    init {
        setTrueItem(GUIItem(Material.LIME_WOOL,1)
                .setDisplay("§a入札する"))
        setFalseItem(GUIItem(Material.RED_WOOL,1)
                .setDisplay("§cキャンセル"))
        setItem(13,
                GUIItem(item)
                        .addLore("§aあなたの入札額：§e${AuctionFunc.getYenString(price.toString())}")
        )
        setOutput{
            val player=it.event.whoClicked as Player
            if(it.boolean){
                gnAuction.bid(player,id,price)
            }
            close(player)
        }
    }
}