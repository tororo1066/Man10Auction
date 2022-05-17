package ltotj.minecraft.man10auctionhouse.auction.menu.inauction

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.system.SPAuction
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import java.lang.Math.min

class SpecialAuctionMenu(parent:MenuGUI,private val page:Int,val spAuction:SPAuction):MenuGUI(Main.plugin,6,"${page}ページ目",parent,"spauction${page}",false) {


        init {

        setClickEvent { gui, inventoryClickEvent ->
            inventoryClickEvent.isCancelled=true
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
        if(spAuction.itemID.size>45*(page-1)){
            for((slot, i) in (45*(page-1) until min(spAuction.itemID.size,45*page)).withIndex()){
                setItem(slot, spAuction.exhibits[spAuction.itemID[i]]?.getIcon()?:continue)
            }
        }
        else{
            for(uuid in invPlayerList){
                val player= Bukkit.getPlayer(uuid)?:continue
                if(!openSiblingGUI("spauction${page-1}",player)){
                    close(player)
                }
            }
            parent()!!.deleteChild("spauction${page}")
        }
        renderGUI()
    }

    fun clearItems(){
        for(i in 0 until 45){
            removeItem(i)
        }
    }

    private fun nextPage(player: Player){
        if(!openSiblingGUI("spauction${page+1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最後のページです")
        }
        else{
            (siblings["spauction${page+1}"]!! as SpecialAuctionMenu).reloadItems()
        }
    }

    private fun returnPage(player: Player){
        if(!openSiblingGUI("spauction${page-1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最初のページです")
        }
        else{
            (siblings["spauction${page-1}"]!! as SpecialAuctionMenu).reloadItems()
        }
    }
}