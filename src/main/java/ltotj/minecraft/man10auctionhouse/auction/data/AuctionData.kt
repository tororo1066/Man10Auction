package ltotj.minecraft.man10auctionhouse.auction.data

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.ValutManager.VaultManager
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class AuctionData(id: Int, item: ItemStack, seller: String, sellerCN: String,val sellerUUID: UUID, reserve: Long, unit: Long, genre: Int) :ItemData(id, item, seller, sellerCN, reserve, unit, genre) {

    var lastBidderName=""
    var lastBidderUUID:UUID?=null
    var biddingUnits=0L
    var previousBidderUUID:UUID?=null
    var previousBiddingUnits=0L
    var isEnd=AtomicBoolean(false)
    var inBidding=AtomicBoolean(false)

    fun isEnd():Boolean{
        return isEnd.get()
    }

    fun isEnd(boolean: Boolean){
        isEnd.set(boolean)
    }

    fun setBidder(player:Player,units:Long){
        previousBidderUUID=lastBidderUUID
        previousBiddingUnits=biddingUnits
        lastBidderName=player.name
        lastBidderUUID=player.uniqueId
        biddingUnits=units
    }

}