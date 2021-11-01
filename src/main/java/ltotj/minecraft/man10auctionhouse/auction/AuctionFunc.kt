package ltotj.minecraft.man10auctionhouse.auction

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.data.ItemData
import ltotj.minecraft.man10auctionhouse.auction.menu.*
import ltotj.minecraft.man10auctionhouse.utility.MySQLManager.MySQLManager
import net.kyori.adventure.text.Component
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

object AuctionFunc {

    fun broadcastActionBarInVenue(string: String){
        for (player in Main.venue.players) {
            player.sendActionBar(Component.text(string))
        }
    }

    fun broadcastInVenue(string: String){
        for (player in Main.venue.players) {
            player.sendMessage(Component.text(string))
        }
    }

    fun createMainGUIs(): MainMenu {
        val menu= MainMenu()
        PreRegistrationMenu(menu)
        Main.executor.execute {

            val mysql= MySQLManager(Main.plugin)

            val result=mysql.select(arrayOf("id","item","unit_price","seller_name","seller_custom_name"),"listing_data","where genre=0 and item_status in (0,1)")
            val size=1+ (result.size()/45)
            result.forEach {
                val itemData= ItemData(it.getInt("id"),itemFromBase64(it.getString("item"))?:return@forEach
                        ,it.getString("seller_name"),it.getString("seller_custom_name"),it.getInt("reserve_price"),it.getInt("unit_price"),0)
                GeneralExhibitMenu.addItem(it.getInt("id"),itemData)
            }
            for(i in 0 until size){
                GeneralExhibitMenu(menu,false,i+1)
            }

            val opSpResult=mysql.select(arrayOf("id","genre","item","reserve_price","unit_price","seller_name","seller_custom_name"),"listing_data","where genre in (1,2) and item_status in (0,1)")
            val opSpSize=1+ (opSpResult.size()/45)
            var spSizeCount=0
            opSpResult.forEach {
                val itemData= ItemData(it.getInt("id"),itemFromBase64(it.getString("item"))?:return@forEach
                        ,it.getString("seller_name"),it.getString("seller_custom_name"),it.getInt("reserve_price"),it.getInt("unit_price"),it.getInt("genre"))
                OPSpecialExhibitMenu.addItem(it.getInt("id"),itemData)
                if(it.getInt("genre")==2){
                    SpecialExhibitMenu.addItem(it.getInt("id"),itemData)
                    spSizeCount++
                }
            }
            for(i in 0 until opSpSize){
                OPSpecialExhibitMenu(menu,false,i+1)
            }
            for(i in 0 until 1 +(spSizeCount/45)){
                SpecialExhibitMenu(menu,false,i+1)
            }
        }
        return menu
    }


    private fun countPlayerAirPocket(player: Player):Int{
        var count=0
        for(i in 0 until 36){
            if(player.inventory.contents[i]==null)count++
        }
        return count
    }

    fun getDateForMySQL(date: Date): String? {
        val df: DateFormat = SimpleDateFormat("yyyy-MM-dd HHH:mm:ss")
        return df.format(date)
    }

    fun getYenString(money: String): String {
        if (money.isEmpty()) {
            return "0"
        }
        val yen = StringBuilder()
        val first = (money.length + 2) % 3 + 1
        for (i in 0 until 1 + (money.length - 1) / 3) {
            if (i == 0) {
                yen.append(money.substring(0, first))
            } else {
                yen.append(",").append(money.substring(first + 3 * (i - 1), first + 3 * i))
            }
        }
        yen.append("円")
        return yen.toString()
    }

    fun playSoundInVenue(sound: Sound,volume:Float,pitch:Float){
        for (player in Main.venue.players) {
            player.playSound(player.location,sound,volume,pitch)
        }
    }

    fun itemFromBase64(data: String): ItemStack? = try {
        val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
        val dataInput = BukkitObjectInputStream(inputStream)
        val items = arrayOfNulls<ItemStack>(dataInput.readInt())

        // Read the serialized inventory
        for (i in items.indices) {
            items[i] = dataInput.readObject() as ItemStack
        }

        dataInput.close()
        items[0]
    } catch (e: Exception) {
        null
    }

    @Throws(IllegalStateException::class)
    fun itemToBase64(item: ItemStack?): String? {
        if(item==null||item.type== Material.AIR)return null
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)
            val items = arrayOfNulls<ItemStack>(1)
            items[0] = item
            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }

            dataOutput.close()

            return Base64Coder.encodeLines(outputStream.toByteArray())

        } catch (e: Exception) {
            throw IllegalStateException("Unable to save item stacks.", e)
        }
    }

    fun returnItem(player: Player, item: ItemStack){
        Main.plugin.server.scheduler.runTask(Main.plugin, Runnable{
            val pocket = countPlayerAirPocket(player)
            if (pocket == 0) player.world.dropItem(player.location, item)
            else {
                player.inventory.addItem(item)
            }
        })
    }

}