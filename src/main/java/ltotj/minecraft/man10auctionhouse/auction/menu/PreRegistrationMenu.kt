package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Material
import org.bukkit.entity.Player

class PreRegistrationMenu(menu:MenuGUI):MenuGUI(Main.plugin, 1, "§a§l出品のカテゴリを選択してください", menu,"register",false) {

    init{
        val generallyRegisterButton= GUIItem(Material.ENDER_PEARL,1)
                .setDisplay("§e一般カテゴリで出品する")
                .setLore(arrayOf("§d目玉商品としての出品を希望しない"
                        ,"§d場合はこちらをクリックしてください"
                        ,"§a一人§e§l3点§aまで"))
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    val grGUI=GeneralRegistrationMenu(player)
                    guiItem.gui().forceChangeGUI(player,grGUI)
                }

        val speciallyRegisterButton= GUIItem(Material.ENDER_EYE,1)
                .setDisplay("§e目玉商品として出品を希望する")
                .setLore(arrayOf("§d目玉商品としての出品を希望する"
                        ,"§d場合はこちらをクリックしてください"
                        ,"§d目玉商品としての出品が認められなかった"
                        ,"§d場合、そのまま返却されます"
                        ,"§4※明らかに目玉商品になり得ないものの出"
                        ,"§4  品は警告の対象となります"
                        ,"§4  改善が見られない場合、オークションへの"
                        ,"§4  出品を制限される場合があります"
                        ,"§a一人§e§l2点§aまで"))
                .setEvent { guiItem, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    inventoryClickEvent.isCancelled=true
                    val srGUI=SpecialRegistrationMenu(player)
                    guiItem.gui().forceChangeGUI(player,srGUI)
                }

                setItem(2,generallyRegisterButton)
                        .setItem(6,speciallyRegisterButton)
    }

}