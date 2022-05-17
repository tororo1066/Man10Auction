package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Material
import org.bukkit.entity.Player

class MainMenu:MenuGUI(Main.plugin,1, Main.pluginTitle) {

    init{
        val sellButton= GUIItem(Material.CREEPER_HEAD,1)
                .setDisplay("§e出品する")
                .setLore(arrayOf("§dオークションに出品するアイテムを登録します"
                        ,"§4※オークションは月に一度のペースで行われます"
                        ,"§4 また、オークションが開催される三日前から登録不可になります"))
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    if(Main.inAuction || Main.duringPublication){
                        player.sendMessage("${Main.pluginTitle}§4現在出品を行うことはできません")
                        return@setEvent
                    }
                    (guiItem.gui() as MenuGUI).openChildGUI("register",player)
                }

        val buyButton= GUIItem(Material.GOLD_INGOT,1)
                .setDisplay("§e出品されているアイテムを見る")
                .setLore(arrayOf("§dオークションに出品されている"
                        ,"§dアイテム一覧を表示します"))
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    if(Main.inAuction){
                        ((guiItem.gui()) as MenuGUI).openChildGUI("gnauction1",player)
                        return@setEvent
                    }
                    else{
                        if (!player.hasPermission("man10auction.op")) {
                            player.sendMessage("${Main.pluginTitle}§4現在、オークションアイテム公開期間ではありません")
                            return@setEvent
                        }

                        val menu = (guiItem.gui()) as MenuGUI

                        if (menu.children().containsKey("generalExhibit1")) {
                            (menu.children()["generalExhibit1"]!! as GeneralExhibitMenu).reloadItems()
                        }
                        menu.openChildGUI("generalExhibit1", player)
                    }
                }

        val mainButton= GUIItem(Material.NETHER_STAR,1)
                .setDisplay("§e§l目玉商品をチェックする")
                .setLore(arrayOf("§dオークションの目玉商品をチェックします"))
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    val menu=(guiItem.gui()) as MenuGUI

                    if(Main.inAuction){
                        menu.openChildGUI("spauction1",player)
                        return@setEvent
                    }
                    if(!Main.duringPublication&&!player.hasPermission("man10auction.op")){
                        player.sendMessage("${Main.pluginTitle}§4現在、オークションアイテム公開期間ではありません")
                        return@setEvent
                    }

                    if(player.hasPermission("man10auction.op")&&inventoryClickEvent.isShiftClick){
                        if(menu.children().containsKey("opSpecialExhibit1")){
                            (menu.children()["opSpecialExhibit1"]!! as OPSpecialExhibitMenu).reloadItems()
                        }
                        menu.openChildGUI("opSpecialExhibit1",player)
                        return@setEvent
                    }

                    if(menu.children().containsKey("specialExhibit1")){
                        (menu.children()["specialExhibit1"]!! as SpecialExhibitMenu).reloadItems()
                    }
                    menu.openChildGUI("specialExhibit1",player)
                }

        val storageButton= GUIItem(Material.CHEST,1)
                .setDisplay("§e出品登録したアイテムを確認する")
                .setLore(arrayOf("§dオークションに出品登録したアイテムを確認/登録解除します"
                        ,"§4※オークションは月に一度のペースで行われます"
                        ,"§4  また、オークションが開催される三日前から登"
                        ,"§4  録解除不可になります"))
                .setEvent { _, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    val key="${player.name}_storage"

                    //mysql処理の関係上menuクラスのinitにopen処理がある
                    val storageGUI=PlayerExhibitMenu(player,Main.plugin,6,"§a${player.name}§eの出品アイテム", this,key)
                }

        val receiveButton= GUIItem(Material.ENDER_CHEST,1)
                .setDisplay("§eアイテムを受け取る")
                .setLore(arrayOf("§d落札したアイテムの受け取り画面を開きます"))
                .setEvent { _, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val player=inventoryClickEvent.whoClicked as Player
                    val key="${player.name}_receive"

                    //mysql処理の関係上menuクラスのinitにopen処理がある
                    val receiveGUI=PlayerContainerMenu(player,Main.plugin,6,"§a§l受け取りボックス", this,key)
                    }

        fillItem(GUIItem(Material.WHITE_STAINED_GLASS_PANE,1))
                .setItem(0,sellButton)
                .setItem(2,buyButton)
                .setItem(4,mainButton)
                .setItem(6,storageButton)
                .setItem(8,receiveButton)
    }


}