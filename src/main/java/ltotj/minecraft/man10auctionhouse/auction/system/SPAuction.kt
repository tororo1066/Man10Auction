package ltotj.minecraft.man10auctionhouse.auction.system

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.Main.Companion.plugin
import ltotj.minecraft.man10auctionhouse.Main.Companion.venue
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.data.AuctionData
import ltotj.minecraft.man10auctionhouse.auction.menu.inauction.GeneralAuctionMenu
import ltotj.minecraft.man10auctionhouse.auction.menu.inauction.SpecialAuctionMenu
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.man10auctionhouse.utility.TimeManager.TimerManager
import ltotj.minecraft.man10auctionhouse.utility.ValutManager.VaultManager
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Server
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.Executors
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.min

class SPAuction{


    private val spAuctionThread= Executors.newSingleThreadExecutor()
    val exhibits=HashMap<Int,AuctionData>()
    val itemID=ArrayList<Int>()
    private var currentItemId=-1
    private val vault= VaultManager(Main.plugin)
    private val spMySQL=MySQLManager(Main.plugin)

    private var available=false
    private var remainTime=0
    private val timer=TimerManager()
    private var itemCount=0

    init {

        val result=spMySQL.select(arrayOf("id","item","reserve_price","unit_price","seller_name","seller_custom_name","seller_uuid"),"listing_data","where genre=2 and item_status=1")
        val size=1+ (result.size()/45)
        result.forEach {
            exhibits[it.getInt("id")]= AuctionData(it.getInt("id"),AuctionFunc.itemFromBase64(it.getString("item"))?: ItemStack(Material.BARRIER)
                    ,it.getString("seller_name"),it.getString("seller_custom_name"), UUID.fromString(it.getString("seller_uuid")),it.getLong("reserve_price"),it.getLong("unit_price"),2)
            itemID.add(it.getInt("id"))
        }
        for(i in 0 until size){
            SpecialAuctionMenu(Main.mainGUI,i+1,this)
        }

        timer.setRemainingTime(45)
                .addStartEvent{
                    val data = exhibits[currentItemId]?:return@addStartEvent
                    plugin.server.broadcast(Component.text("${Main.pluginTitle}§f§l${data.item.itemMeta.displayName}§aのオークションが始まります！"), Server.BROADCAST_CHANNEL_USERS)
                    Thread.sleep(2000)
                    AuctionFunc.broadcastInVenue("${Main.pluginTitle}§aオークションの情報を表示します・・・")
                    Thread.sleep(2000)
                    AuctionFunc.broadcastInVenue("${Main.pluginTitle}§d出品者：§c${data.sellerCN}")
                    Thread.sleep(2000)
                    AuctionFunc.broadcastInVenue(Component.text("${Main.pluginTitle}§d商品名：§f§l").append(data.item.displayName()))
                    Thread.sleep(2000)
                    AuctionFunc.broadcastInVenue("${Main.pluginTitle}§d一口：§e${AuctionFunc.getYenString(data.unit.toString())}")
                    Thread.sleep(2000)
                    AuctionFunc.broadcastInVenue("${Main.pluginTitle}§aそれでは、オークション開始！")
                    available=true
                }
                .addIntervalEvent(1) {
                    val item=exhibits[currentItemId]?.item?:ItemStack(Material.STONE)
                    AuctionFunc.broadcastActionBarInVenue(item.displayName().append(Component.text(" §a§lのオークション中…残り§c§l${it}秒")))
                    when(it){
                        30,20,10->{
                            AuctionFunc.playSoundInVenue(Sound.BLOCK_BELL_USE,2F,0.5F)
                            AuctionFunc.broadcastInVenue("${Main.pluginTitle}§a残り§c${it}秒")
                        }
                    }
                }
                .addEndEvent {
                    available=false
                    val item=exhibits[currentItemId]?.item?:ItemStack(Material.STONE)
                    AuctionFunc.broadcastActionBarInVenue(item.displayName().append(Component.text(" §a§lのオークション中…残り§c§l0秒")))
                    var data = exhibits[currentItemId] ?: return@addEndEvent
                    data.isEnd(true)
                    AuctionFunc.broadcastInVenue("${Main.pluginTitle}§a入札が終了しました！")
                    Thread.sleep(2000)
                    Main.plugin.server.broadcast(Component.text("${Main.pluginTitle}§c§l${data.lastBidderName}§aが").append(data.item.displayName()).append(Component.text("§aを§e§l${AuctionFunc.getYenString((data.biddingUnits * data.unit).toString())}§aで落札しました！")), Server.BROADCAST_CHANNEL_USERS)
                    //ここに音入れる？

                    //ここにmysqlの処理

                    Thread.sleep(2000)

                    itemCount++
                    if(itemCount<exhibits.size){
                        currentItemId=itemID[itemCount]
                        data = exhibits[currentItemId] ?: return@addEndEvent
                        AuctionFunc.broadcastInVenue(Component.text("${Main.pluginTitle}§a続いては§f§l").append(data.item.displayName()).append(Component.text("§aのオークションです")))
                        timer.setRemainingTime(45)
                        Thread.sleep(2000)
                        timer.forcedStart()
                    }
                    else{
                        Main.plugin.server.broadcast(Component.text("${Main.pluginTitle}§a本日のメインオークションは全て終了しました"))
                    }
                }
    }

    fun addTime(second:Int){
        timer.addRemainingTime(second)
    }

    fun getData():AuctionData?{
        return exhibits[currentItemId]
    }

    fun start(){
        if(itemID.isEmpty())return
        currentItemId=itemID[0]
        timer.start()
    }

    fun bid(player: Player, units:Long){
        //ここにタイマー停止処理
        //処理が終わるタイミングで再開処理
        if(!available){
            player.sendMessage("${Main.pluginTitle}§4入札時間ではありません")

            return
        }
        //シングルのスレッドプールなので処理中かどうかのbooleanは使わなくてOK(のハズ）
        spAuctionThread.execute {
            val data = exhibits[currentItemId] ?: return@execute
//            if (player.uniqueId == data.lastBidderUUID) {
//                player.sendMessage("${Main.pluginTitle}§aあなたは最終入札者です")
//                return@execute
//            }
//            if(player.uniqueId==data.sellerUUID){
//                player.sendMessage("${Main.pluginTitle}§4自分の出品物を入札することはできません")
//                return@execute
//            }
            //前回落札額との比較がない
            if (!vault.withdraw(player, units * data.unit.toDouble())) {
                player.sendMessage("${Main.pluginTitle}§4所持金が不足しています")
                return@execute
            }
            if (spMySQL.execute("insert into bidding_data(auction_id,listing_id,bidder_name,bidder_uuid,bidding_price,bidding_date) " +
                            "values(${Main.auctionId},${currentItemId},'${player.name}','${player.uniqueId}',${units*data.unit},${AuctionFunc.getDateForMySQL(Date())});")) {
                data.setBidder(player,units)
                timer.setRemainingTime(45)
                Main.plugin.server.broadcast(Component.text("${Main.pluginTitle}§c§l${player.name}§aが§e§l${AuctionFunc.getYenString((units * data.unit).toString())}§aで入札！"), Server.BROADCAST_CHANNEL_USERS)
                val preP=Bukkit.getPlayer(data.previousBidderUUID?:return@execute)?:return@execute
                if(spMySQL.execute("update bidding_data set money_status=true order by id desc limit 1 where bidder_uuid='${preP.uniqueId}' and listing_id=${currentItemId};")){
                    vault.deposit(player,data.unit*units.toDouble())
                    preP.sendMessage("${Main.pluginTitle}§a入札金が返還されました")
                }
            }
            else{
                player.sendMessage("${Main.pluginTitle}§4入札エラー")
                vault.deposit(player,units * data.unit.toDouble())
            }
        }
    }


}