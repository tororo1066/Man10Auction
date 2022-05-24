package ltotj.minecraft.man10auctionhouse.auction.system

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.data.AuctionData
import ltotj.minecraft.man10auctionhouse.auction.menu.inauction.GeneralAuctionMenu
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import ltotj.minecraft.man10auctionhouse.utility.TimeManager.TimerManager
import ltotj.minecraft.man10auctionhouse.utility.ValutManager.VaultManager
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*

class GNAuction{

    val exhibits=HashMap<Int,AuctionData>()
    val itemListKeys=ArrayList<Int>()
    private val timer=TimerManager()

    init {
        Main.executor.execute {
            val mysql=MySQLManager(Main.plugin)
            val result=mysql.select(arrayOf("id","item","reserve_price","unit_price","seller_name","seller_custom_name","seller_uuid"),"listing_data","where genre=0 and item_status=1")
            val size=1+ (result.size()/45)
            result.forEach {
                exhibits[it.getInt("id")]= AuctionData(it.getInt("id"),AuctionFunc.itemFromBase64(it.getString("item"))?: ItemStack(Material.BARRIER)
                        ,it.getString("seller_name"),it.getString("seller_custom_name"), UUID.fromString(it.getString("seller_uuid")),it.getLong("reserve_price"),it.getLong("unit_price"),0)
                itemListKeys.add(it.getInt("id"))
            }
            for(i in 0 until size){
                GeneralAuctionMenu(Main.mainGUI,i+1,this)
            }
        }
    }



    fun getData(id:Int):AuctionData?{
        return exhibits[id]
    }

    fun bid(player:Player,id:Int,price:Long){
        val data=exhibits[id]?:return
        if(data.isEnd()){
            player.sendMessage("${Main.pluginTitle}§4入札可能期間ではありません")
            return
        }
        if(data.inBidding.get()){
            player.sendMessage("${Main.pluginTitle}§a他プレイヤーの入札処理中です  時間をあけて再度お試しください")
            return
        }
        data.inBidding.set(true)
        Main.executor.execute {
            val vault=VaultManager(Main.plugin)
            if (player.uniqueId == data.lastBidderUUID) {
                player.sendMessage("${Main.pluginTitle}§aあなたは最終入札者です")
                return@execute
            }
//            if(player.uniqueId==data.sellerUUID){
//                player.sendMessage("${Main.pluginTitle}§4自分の出品物を入札することはできません")
//                return@execute
//            }
            if(price%data.unit!=0L){
                player.sendMessage("${Main.pluginTitle}§4入札額は一口当たりの金額の倍数を入力してください")
                return@execute
            }
            val units=(price/data.unit)
            if(data.biddingUnits>=units){
                player.sendMessage("${Main.pluginTitle}§4現在の入札額以上の口数・金額で入札してください")
                return@execute
            }
            if (!vault.withdraw(player, price.toDouble())) {
                player.sendMessage("${Main.pluginTitle}§4所持金が不足しています")
                return@execute
            }
            val mysql=MySQLManager(Main.plugin)
            if (mysql.execute("insert into bidding_data(auction_id,listing_id,bidder_name,bidder_uuid,bidding_price,bidding_date) " +
                            "values(${Main.auctionId},${data.id},'${player.name}','${player.uniqueId}',${price},'${AuctionFunc.getDateForMySQL(Date())}');")) {
                data.setBidder(player,units)
                data.inBidding.set(false)

                player.sendMessage("${Main.pluginTitle}§a${price}円入札しました！")

                data.previousBidderUUID?:return@execute
                Main.man10Bank.deposit(data.previousBidderUUID!!,data.unit*units.toDouble(),"Man10AuctionHouse General Other Player Bid","Man10AuctionHouse 通常オークションの§f${data.item.itemMeta.displayName}§eに対するほかのプレイヤーの入札")
            }
            else{
                data.setBidder(player,units)
                player.sendMessage("${Main.pluginTitle}§4入札エラー")
                vault.deposit(player,units * data.unit.toDouble())
            }
        }
    }

}