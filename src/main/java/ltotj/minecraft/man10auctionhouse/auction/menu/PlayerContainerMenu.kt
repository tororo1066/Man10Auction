package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class PlayerContainerMenu(val player: Player, plugin: JavaPlugin, row:Int, title:String, parent: MenuGUI, key:String): MenuGUI(plugin,row,title,parent,key,true) {

    var availableMySQL=true

    init {
        setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }

        Main.executor.execute {

            val mysql= MySQLManager(plugin)
            val buyResult=mysql.select(arrayOf("id","item"),"listing_data","where suc_bidder_uuid='${player.uniqueId}' and item_status=5")
            val unsoldResult=mysql.select(arrayOf("id","item"),"listing_data","where seller_uuid='${player.uniqueId}' and item_status=4")

            var slot=0

            buyResult.forEach {

                val id=it.getInt("id")
                val item= AuctionMenuFunc.itemFromBase64(it.getString("item"))?:return@forEach
                val icon= GUIItem(item.clone())

                icon.setEvent { _, inventoryClickEvent ->
                            if (inventoryClickEvent.isShiftClick&&availableMySQL) {
                                availableMySQL=false
                                Main.executor.execute {
                                    val player=inventoryClickEvent.whoClicked as Player

                                    if(mysql.execute("update listing_data set item_status=6 where id=$id;")){
                                        removeItem(inventoryClickEvent.slot)
                                        reloadItem(inventoryClickEvent.slot)
                                        AuctionMenuFunc.returnItem(player, item)
                                        player.sendMessage("${Main.pluginTitle}§a§l入札したアイテムを受け取りました！")
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

            unsoldResult.forEach {
                val id=it.getInt("id")
                val item= AuctionMenuFunc.itemFromBase64(it.getString("item"))?:return@forEach
                val icon= GUIItem(item.clone())

                icon.addLore(arrayOf("§4未売却品"))
                        .setEvent { _, inventoryClickEvent ->
                    if (inventoryClickEvent.isShiftClick&&availableMySQL) {
                        availableMySQL=false
                        Main.executor.execute {
                            val player=inventoryClickEvent.whoClicked as Player

                            if(mysql.execute("update listing_data set item_status=7 where id=$id;")){
                                removeItem(inventoryClickEvent.slot)
                                reloadItem(inventoryClickEvent.slot)
                                AuctionMenuFunc.returnItem(player, item)
                                player.sendMessage("${Main.pluginTitle}§a§l未売却アイテムを受け取りました")
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
        for(i in slot until size){
            setItem(slot,getItem(slot+1)!!)
        }
        removeItem(size)
        renderGUI()
    }


}