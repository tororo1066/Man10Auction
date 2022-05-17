package ltotj.minecraft.man10auctionhouse.auction.menu.inauction

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.menu.GeneralExhibitMenu
import ltotj.minecraft.man10auctionhouse.auction.system.GNAuction
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.menu.NumericGUI
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.min

class GeneralAuctionMenu(parent: MenuGUI, private val page:Int,private val gnAuction: GNAuction): MenuGUI(Main.plugin,6,"§b一般出品一覧:${page}ページ目",parent,"gnauction${page}",false) {

    init {

        setClickEvent { gui, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
            val item=gui.getItem(inventoryClickEvent.slot)?:return@setClickEvent
            val id=item.getNBTInt("id",Main.plugin)

            if(id>=0){
                forceChangeGUI(inventoryClickEvent.whoClicked as Player,
                        NumericGUI(Main.plugin,"入札口数を入力：一口当たり${gnAuction.exhibits[id]?.unit?:0}円")
                                .setOutput{
                                    forceChangeGUI(inventoryClickEvent.whoClicked as Player,GNBiddingGUI(item,id,it*(gnAuction.exhibits[id]?.unit?:0).toLong(),gnAuction))
                                }
                )
            }
        }

        val nextButton= GUIItem(Material.LIME_WOOL,1)
                .setDisplay("§a次のページへ")
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    nextPage(player)
                }

        val returnButton= GUIItem(Material.RED_WOOL,1)
                .setDisplay("§c前のページへ")
                .setEvent { _, inventoryClickEvent ->
                    val player=inventoryClickEvent.whoClicked as Player
                    returnPage(player)
                }

        val reloadButton= GUIItem(Material.NETHER_STAR,1)
                .setEvent { _, _ ->
                    reloadItems()
                }

        setItems(arrayOf(45,46,47),returnButton)
        setItems(arrayOf(48,50), GUIItem(Material.BLUE_STAINED_GLASS_PANE,1))
        setItems(arrayOf(51,52,53),nextButton)
        setItem(49,reloadButton)
    }

    fun reloadItems(){
        clearItems()
        if(gnAuction.itemListKeys.size>45*(page-1)){
            println(gnAuction.itemListKeys.size)
            for((slot, i) in (45*(page-1) until min(gnAuction.itemListKeys.size,45*page)).withIndex()){
                setItem(slot, gnAuction.exhibits[gnAuction.itemListKeys[i]]?.getIcon()?:continue)
            }
        }
        else{
            for(uuid in invPlayerList){
                val player= Bukkit.getPlayer(uuid)?:continue
                if(!openSiblingGUI("gnauction${page-1}",player)){
                    close(player)
                }
            }
            parent()!!.deleteChild("gnauction${page}")
        }
        renderGUI()
    }

    fun clearItems(){
        for(i in 0 until 45){
            removeItem(i)
        }
    }

    private fun nextPage(player: Player){
        if(!openSiblingGUI("gnauction${page+1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最後のページです")
        }
        else{
            (siblings["gnauction${page+1}"]!! as GeneralExhibitMenu).reloadItems()
        }
    }

    private fun returnPage(player: Player){
        if(!openSiblingGUI("gnauction${page-1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最初のページです")
        }
        else{
            (siblings["gnauction${page-1}"]!! as GeneralExhibitMenu).reloadItems()
        }
    }

}