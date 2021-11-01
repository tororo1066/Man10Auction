package ltotj.minecraft.man10auctionhouse.auction.menu

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.data.ItemData
import ltotj.minecraft.man10auctionhouse.utility.GUIManager.GUIItem
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.testplugin.GUIManager.menu.MenuGUI
import net.wesjd.anvilgui.AnvilGUI
import org.bukkit.Material
import org.bukkit.entity.Player
import java.util.*

class GeneralRegistrationMenu(val player: Player): MenuGUI(Main.plugin,1,"§a§l出品の詳細設定") {

  init{

        val priceButton= GUIItem(Material.GOLD_INGOT,1)
                .setDisplay("§e最低落札額を設定する")
                .setLore(arrayOf("§d現在のステータス:§e1円"
                        ,"§dクリックで最低落札額を設定できます"))
                .setNBTInt("reservePrice",1,Main.plugin)
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true

                    AnvilGUI.Builder()
                            .plugin(Main.plugin)
                            .onClose {
                                guiItem.gui().open(player)
                            }
                            .onComplete { _, text ->
                                val reserve=text.toIntOrNull()
                                if (reserve!=null&&reserve>0) {
                                    val limit=Main.plugin.config.getInt("reserveUpperLimit")
                                    if(reserve<=limit) {
                                        guiItem.setNBTInt("reservePrice", reserve, Main.plugin)
                                                .setLore(arrayOf("§d現在のステータス:§e${AuctionFunc.getYenString(text)}", "§dクリックで最低落札額を設定できます"))
                                        AnvilGUI.Response.close()
                                    }
                                    else{
                                        AnvilGUI.Response.text("上限は${limit}円です")
                                    }
                                }
                                else {
                                    AnvilGUI.Response.text("金額は0以上の整数で入力してください")
                                }
                            }
                            .itemLeft(
                                    GUIItem(Material.GOLD_INGOT, 1)
                                            .setDisplay("§e-最低落札金額設定用アイテム-")
                            )
                            .text("金額を入力")
                            .open(player)
                }

        val oneUnitButton= GUIItem(Material.GOLD_NUGGET,1)
                .setDisplay("§e一口の価格を設定する")
                .setLore(arrayOf("§d現在のステータス:§e5,000円"
                        ,"§dクリックで一口あたりの金額を設定できます"))
                .setNBTInt("unitPrice",5000,Main.plugin)
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled = true

                    AnvilGUI.Builder()
                            .plugin(Main.plugin)
                            .onClose {
                                guiItem.gui().open(player)
                            }
                            .onComplete { _, text ->
                                val unit=text.toIntOrNull()
                                if (unit!=null&&unit>0) {
                                    val limit=Main.plugin.config.getInt("unitUpperLimit")
                                    if(unit<=limit) {
                                        guiItem.setNBTInt("unitPrice", unit, Main.plugin)
                                                .setLore(arrayOf("§d現在のステータス:§e${AuctionFunc.getYenString(text)}", "§dクリックで一口あたりの金額を設定できます"))
                                        AnvilGUI.Response.close()
                                    }
                                    else{
                                        AnvilGUI.Response.text("上限は${limit}円です")
                                    }
                                } else {
                                    AnvilGUI.Response.text("金額は0以上の整数で入力してください")
                                }
                            }
                            .itemLeft(
                                    GUIItem(Material.GOLD_NUGGET, 1)
                                            .setDisplay("§e-名前変更用-")
                            )
                            .text("金額を入力")
                            .open(player)
                }

        val registerButton= GUIItem(Material.HOPPER,1)
                .setDisplay("§e出品アイテム登録画面に進む")
                .setEvent { guiItem, inventoryClickEvent ->
                    inventoryClickEvent.isCancelled=true
                    val gui=guiItem.gui()

                    val reservePrice=gui.getItem(0)!!.getNBTInt("reservePrice",Main.plugin)
                    val unitPrice=gui.getItem(4)!!.getNBTInt("unitPrice",Main.plugin)

                    val finalGUI= MenuGUI(Main.plugin,1,"§a§l出品アイテムを入れた後、羽をクリックで登録")

                    val glass=GUIItem(Material.BLUE_STAINED_GLASS_PANE,1)
                            .setEvent { _, ginventoryClickEvent ->
                                ginventoryClickEvent.isCancelled=true
                            }
                    val fregisterButton= GUIItem(Material.FEATHER,1)
                            .setDisplay("§a§l以下の条件で出品登録する")
                            .setLore(arrayOf("§d最低落札金額：§e${AuctionFunc.getYenString(reservePrice.toString())}"
                                    ,"§d一口あたりの入札額：§e${AuctionFunc.getYenString(unitPrice.toString())}"))
                            .setEvent { fguiItem, finventoryClickEvent ->
                                //val fplayer=inventoryClickEvent.whoClicked as Player
                                finventoryClickEvent.isCancelled=true
                                val item=fguiItem.gui().getInvItem(4)
                                if(item.type==Material.AIR||item==null){
                                    player.sendMessage("${Main.pluginTitle}§4出品するアイテムを入れてください")
                                }
                                fguiItem.gui().removeInvItem(item)
                                        .renderItem(inventoryClickEvent.slot)
                                        .close(player)

                                //ここにmysqlのinsert処理 失敗でアイテム返却
                                Main.executor.execute {

                                    val mysql = MySQLManager(Main.plugin)
                                    val result=mysql.select("id","auction_data","order by id desc limit 1")
                                    if(!result.next()){
                                        player.sendMessage("${Main.pluginTitle}§4オークションが見つかりませんでした")
                                        AuctionFunc.returnItem(player, item)
                                        return@execute
                                    }

                                    //fguiItemから諸々のデータ抽出

                                    var auctionId=0
                                    result.execute { auctionId=it.getInt("id") }
                                    if(mysql.getInsertQuery("listing_data")
                                                    .addInt("auction_id",auctionId)
                                                    .addInt("genre",0)
                                                    .add("item", AuctionFunc.itemToBase64(item) ?:"error")
                                                    .addInt("reserve_price",reservePrice)
                                                    .addInt("unit_price",unitPrice)
                                                    .add("seller_name",player.name)
                                                    .add("seller_uuid",player.uniqueId.toString())
                                                    .add("seller_custom_name",player.name)
                                                    .add("register_date", AuctionFunc.getDateForMySQL(Date()) ?:"")
                                                    .execute()){
                                        player.sendMessage("${Main.pluginTitle}§a§l出品アイテムを登録しました！")
                                        val idResult=mysql.select("id","listing_data","order by id desc limit 1 where seller_uuid='${player.uniqueId}' and genre=1")
                                        if(!idResult.next()){
                                            println("${Main.pluginTitle}出品ID取得でエラーが発生しました")
                                            return@execute
                                        }
                                        var id=0
                                        idResult.execute { id=it.getInt("id") }
                                        GeneralExhibitMenu.addItem(id, ItemData(id,item,player.name,player.name,reservePrice,unitPrice,0))

                                    }
                                    else{
                                        player.sendMessage("${Main.pluginTitle}§4登録エラー")
                                        AuctionFunc.returnItem(player, item)
                                    }
                                }
                            }

                    finalGUI.setItem(8,fregisterButton)
                            .setItems(arrayOf(0,1,2,3,5,6,7),glass)
                            .setCloseEvent { _, inventoryCloseEvent ->
                                if(getInvItem(4)!=null&&getInvItem(4).type!=Material.AIR){
                                    AuctionFunc.returnItem(inventoryCloseEvent.player as Player,getInvItem(4))
                                }
                            }
                    gui.changeGUI(player,finalGUI)
                }

        setItem(0,priceButton)
                .setItem(4,oneUnitButton)
                .setItem(8,registerButton)

    }


}