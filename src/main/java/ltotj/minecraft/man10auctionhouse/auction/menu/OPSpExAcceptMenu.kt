package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.data.ItemData
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Material
import org.bukkit.entity.Player

class OPSpExAcceptMenu(itemData: ItemData, private val id:Int, parent:MenuGUI):MenuGUI(Main.plugin,3,"§a緑§0で許可、§c赤§0で却下",parent,"accept${id}",true) {


    init {

        setClickEvent { _, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
        }

        val accept=GUIItem(Material.LIME_WOOL,1)
                .setDisplay("§a§l目玉商品としての出品を許可する")
                .setEvent { _, inventoryClickEvent ->

                    val player=inventoryClickEvent.whoClicked as Player

                    if(MySQLManager(Main.plugin).execute("update listing_data set genre=2 where id=${id}")){
                        SpecialExhibitMenu.addItem(id,itemData)
                        OPSpecialExhibitMenu.itemList[id]?.genre=2
                        player.sendMessage("${Main.pluginTitle}目玉商品の許可完了")
                        (parent as OPSpecialExhibitMenu).reloadItems()
                        close(player)
                    }
                    else{
                        player.sendMessage("${Main.pluginTitle}§4接続エラー")
                    }
                }

        val deny=GUIItem(Material.RED_WOOL,1)
                .setDisplay("§c§l目玉商品としての出品を許可しない")
                .setEvent { _, inventoryClickEvent ->

                    val player=inventoryClickEvent.whoClicked as Player

                    if(MySQLManager(Main.plugin).execute("update listing_data set genre=2 where id=${id}")){
                        player.sendMessage("${Main.pluginTitle}${itemData.seller}の目玉商品出品を却下しました")
                        OPSpecialExhibitMenu.removeItem(id)
                        (parent as OPSpecialExhibitMenu).reloadItems()
                        close(player)
                    }
                    else{
                        player.sendMessage("${Main.pluginTitle}§4接続エラー")
                    }
                }

        setItems(arrayOf(0,1,2,9,10,11,18,19,20),deny)
        setItems(arrayOf(3,4,5,12,14,21,22,23), GUIItem(Material.BLUE_STAINED_GLASS_PANE,1))
        setItems(arrayOf(6,7,8,15,16,17,24,25,26),accept)
        setItem(13,itemData.getIconForOP())

    }


}