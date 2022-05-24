package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.data.ItemData
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import kotlin.math.min

class SpecialExhibitMenu(parent: MenuGUI, isInstant:Boolean, private val page:Int): MenuGUI(Main.plugin,6,"§c目玉出品一覧:${page}ページ目",parent,"specialExhibit${page}",isInstant) {

    companion object{

        val itemList=HashMap<Int, ItemData>()
        val itemListKeys=ArrayList<Int>()

        fun addItem(id:Int,itemData: ItemData){
            itemList[id]=itemData
            itemListKeys.add(id)
        }

        fun removeItem(id:Int){
            itemList.remove(id)
            itemListKeys.remove(id)
        }
    }

    init {

        setClickEvent { _, inventoryClickEvent ->
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
        if(itemListKeys.size>45*(page-1)){
            println(itemListKeys.size)
            for((slot, i) in (45*(page-1) until min(itemListKeys.size,45*page)).withIndex()){
                setItem(slot, itemList[itemListKeys[i]]?.getIcon()?:continue)
            }
        }
        else{
            for(uuid in invPlayerList){
                val player= Bukkit.getPlayer(uuid)?:continue
                if(!openSiblingGUI("specialExhibit${page-1}",player)){
                    close(player)
                }
            }
            parent()!!.deleteChild("specialExhibit${page}")
        }
        renderGUI()
    }

    fun clearItems(){
        for(i in 0 until 45){
            removeItem(i)
        }
    }

    private fun nextPage(player: Player){
        if(!openSiblingGUI("specialExhibit${page+1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最後のページです")
        }
        else{
            (siblings["specialExhibit${page+1}"]!! as SpecialExhibitMenu).reloadItems()
        }
    }

    private fun returnPage(player: Player){
        if(!openSiblingGUI("specialExhibit${page-1}",player)){
            player.sendMessage("${Main.pluginTitle}§4最初のページです")
        }
        else{
            (siblings["specialExhibit${page-1}"]!! as SpecialExhibitMenu).reloadItems()
        }
    }

}