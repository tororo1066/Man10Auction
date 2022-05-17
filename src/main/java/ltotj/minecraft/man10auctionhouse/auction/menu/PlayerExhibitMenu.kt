package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class PlayerExhibitMenu(val player:Player,plugin: JavaPlugin,row:Int,title:String,parent:MenuGUI,key:String): MenuGUI(plugin,row,title,parent,key,true) {

    var availableMySQL=true

    init {
        setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }

        Main.executor.execute {

            val mysql=MySQLManager(plugin)
            val result=mysql.select(arrayOf("id","genre","item","reserve_price","unit_price","seller_custom_name"),"listing_data","where seller_uuid='${player.uniqueId}' and item_status=0")

            var slot=0
            result.forEach {

                val id=it.getInt("id")
                val genre=if(it.getInt("genre")==0) "§e一般出品" else if(it.getInt("genre")==3) "§4運営より許可が降りませんでした" else "§e目玉商品"
                val item= AuctionFunc.itemFromBase64(it.getString("item"))?:return@forEach
                val icon=GUIItem(item.clone())
                val reservePrice=it.getInt("reserve_price")
                val unitPrice=it.getInt("unit_price")
                val sellerCustomName=it.getString("seller_custom_name")

                icon.addLore(arrayOf(genre
                        ,"§d最低落札価格：§e$reservePrice"
                        ,"§d一口あたりの金額：§e$unitPrice"
                        ,"§d出品者名：§c$sellerCustomName"))
                        .setEvent { _, inventoryClickEvent ->
                    if (inventoryClickEvent.isShiftClick&&availableMySQL) {
                        availableMySQL=false
                        Main.executor.execute {
                            val player=inventoryClickEvent.whoClicked as Player

                            if(mysql.execute("update listing_data set item_status=2 where id=$id;")){
                                removeItem(inventoryClickEvent.slot)
                                reloadItem(inventoryClickEvent.slot)
                                AuctionFunc.returnItem(player,item)
                                player.sendMessage("${Main.pluginTitle}§a§l出品を取り下げました")
                            }
                            else{
                                player.sendMessage("${Main.pluginTitle}§4接続エラー")
                            }
                            availableMySQL=true
                        }

                    }
                }
                setItem(slot,icon)
                slot++
            }
            Main.mainGUI.openChildGUI(key,player)
        }

    }

    private fun reloadItem(slot:Int){
        val size=items.size
        for(i in slot until size) {
            setItem(i, getItem(i + 1)!!)
        }
        removeItem(size)
        renderGUI()
    }



}