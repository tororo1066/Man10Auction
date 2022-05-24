package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.data.ItemData
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.menu.TrueOrFalseGUI
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Material
import org.bukkit.entity.Player

class OPSpExAcceptMenu(itemData: ItemData, private val id:Int, parent:MenuGUI):TrueOrFalseGUI(Main.plugin,"§a緑§0で許可、§c赤§0で却下",parent,"accept${id}",true) {


    init {

        setOutput{
            val player=it.event.whoClicked as Player
            if(it.boolean){

                if (itemData.genre == 2){
                    player.sendMessage("${Main.pluginTitle}すでに許可しています")
                    close(player)
                    return@setOutput
                }

                if(MySQLManager(Main.plugin).execute("update listing_data set genre=2 where id=${id}")){
                    SpecialExhibitMenu.addItem(id,itemData)
                    OPSpecialExhibitMenu.itemList[id]?.genre=2
                    val page = 1 + SpecialExhibitMenu.itemListKeys.size / 45
                    if (!Main.mainGUI.children().containsKey("specialExhibit${page}")){
                        SpecialExhibitMenu(Main.mainGUI,false,page)
                    }
                    player.sendMessage("${Main.pluginTitle}目玉商品の許可完了")
                    (parent as OPSpecialExhibitMenu).reloadItems()
                    close(player)
                }
                else{
                    player.sendMessage("${Main.pluginTitle}§4接続エラー")
                }
            }
            else{
                if(MySQLManager(Main.plugin).execute("update listing_data set genre=3 where id=${id}")){
                    player.sendMessage("${Main.pluginTitle}${itemData.seller}の目玉商品出品を却下しました")
                    OPSpecialExhibitMenu.removeItem(id)
                    SpecialExhibitMenu.removeItem(id)
                    (parent as OPSpecialExhibitMenu).reloadItems()
                    close(player)
                }
                else{
                    player.sendMessage("${Main.pluginTitle}§4接続エラー")
                }
            }
        }

        val accept=GUIItem(Material.LIME_WOOL,1)
                .setDisplay("§a§l目玉商品としての出品を許可する")

        val deny=GUIItem(Material.RED_WOOL,1)
                .setDisplay("§c§l目玉商品としての出品を許可しない")

        setFalseItem(deny)
        setTrueItem(accept)
        setItem(13,itemData.getIconForOP())

    }


}