package ltotj.minecraft.man10auctionhouse.command

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc
import ltotj.minecraft.man10auctionhouse.auction.AuctionFunc.itemFromBase64
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandManager
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandObject
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.java.JavaPlugin

class AuctionOPCommand : CommandManager(Main.plugin,"mahop", Main.pluginTitle) {

    init {
        setPermission("man10auction.op")

        //onコマンド
        addFirstArgument(
                CommandObject("on")
                        .setFunction(consumer = {
                            if(!Main.available) {
                                it.first.sendMessage("${Main.pluginTitle}§a再開しました")
                                Main.available=true
                                Main.plugin.config.set("available", true)
                                Main.plugin.saveConfig()
                            }
                            else{
                                it.first.sendMessage("${Main.pluginTitle}§a既に利用可能です")
                            }
                        })
        )

        //offコマンド
        addFirstArgument(
                CommandObject("off")
                        .setFunction(consumer={
                            if(Main.available) {
                                it.first.sendMessage("${Main.pluginTitle}§a停止しました")
                                Main.available=false
                                Main.plugin.config.set("available", false)
                                Main.plugin.saveConfig()
                            }
                            else{
                                it.first.sendMessage("${Main.pluginTitle}§a既に停止されています")
                            }
                        })
        )
        //通常オークション開始
        addFirstArgument(
                CommandObject("gnstart")
                        .setFunction{
                            Main.inAuction=true
                            AuctionFunc.createAuctionGUIs()
                            Main.venue=(it.first as Player).world
                        }
                        .setExplanation("通常オークションを開始します")
        )
        //通常オークション停止
        addFirstArgument(
                CommandObject("gnstop")
                        .setFunction{
                            Main.inAuction=false
                        }
                        .setExplanation("通常オークションを停止します")
        )
        //SPオークションスタート
        addFirstArgument(
                CommandObject("spstart")
                        .setFunction{
                            Main.inAuction=true
                            Main.spAuction!!.start()
                        }
                        .setExplanation("SPオークションを開始します")
        )
        addFirstArgument(
                CommandObject("test")
                        .setFunction{
                            val player=it.first as Player
                            player.inventory.addItem(itemFromBase64("rO0ABXcEAAAAAXNyABpvcmcuYnVra2l0LnV0aWwuaW8uV3JhcHBlcvJQR+zxEm8FAgABTAADbWFw\n" +
                                    "dAAPTGphdmEvdXRpbC9NYXA7eHBzcgA1Y29tLmdvb2dsZS5jb21tb24uY29sbGVjdC5JbW11dGFi\n" +
                                    "bGVNYXAkU2VyaWFsaXplZEZvcm0AAAAAAAAAAAIAAlsABGtleXN0ABNbTGphdmEvbGFuZy9PYmpl\n" +
                                    "Y3Q7WwAGdmFsdWVzcQB+AAR4cHVyABNbTGphdmEubGFuZy5PYmplY3Q7kM5YnxBzKWwCAAB4cAAA\n" +
                                    "AAR0AAI9PXQAAXZ0AAR0eXBldAAEbWV0YXVxAH4ABgAAAAR0AB5vcmcuYnVra2l0LmludmVudG9y\n" +
                                    "eS5JdGVtU3RhY2tzcgARamF2YS5sYW5nLkludGVnZXIS4qCk94GHOAIAAUkABXZhbHVleHIAEGph\n" +
                                    "dmEubGFuZy5OdW1iZXKGrJUdC5TgiwIAAHhwAAAKqnQAEVdISVRFX1NIVUxLRVJfQk9Yc3EAfgAA\n" +
                                    "c3EAfgADdXEAfgAGAAAABXEAfgAIdAAJbWV0YS10eXBldAAMZGlzcGxheS1uYW1ldAAIaW50ZXJu\n" +
                                    "YWx0AA1ibG9ja01hdGVyaWFsdXEAfgAGAAAABXQACEl0ZW1NZXRhdAALVElMRV9FTlRJVFl0AIh7\n" +
                                    "ImV4dHJhIjpbeyJib2xkIjpmYWxzZSwiaXRhbGljIjpmYWxzZSwidW5kZXJsaW5lZCI6ZmFsc2Us\n" +
                                    "InN0cmlrZXRocm91Z2giOmZhbHNlLCJvYmZ1c2NhdGVkIjpmYWxzZSwidGV4dCI6IuS/rue5le+8\n" +
                                    "k+WGiu+8gSJ9XSwidGV4dCI6IiJ9dAE4SDRzSUFBQUFBQUFBLzYyUVRVL0NVQkJGYjF0SXRBdU1I\n" +
                                    "ekg2VjFoaVdMakdmVk5lcDJYUzF4bDhUREg4ZTJ2YXBFUVJOcXp2dVhmT2V5bVFZcmJ3NnVxbEdO\n" +
                                    "dmhJNjl1TVgwM2FuWXBnQ1RDWk9YVmNJT1lDN3cwTE9SQ1h0cWN4RzF5TVNxeXRXcWRJckdmNXNQ\n" +
                                    "S05GQ3g3TU9HeFBxWnZ2NTBWQzlMZHR5TkhHSWtmdTh4R1U0OGprelRldVBkUm0xQUl2eVoyUVkx\n" +
                                    "Y3NZcUE1T2d3Nlp2Mm9wRkdPU2pxOGkvanZXU0EyVW5UdmZnM1pHZWZsSDQ5Y0Q3TVE2OE5TN29q\n" +
                                    "SHA4RmZVTFJzOWovTm15cTdOdUpGVDAvNiszc2c2VTF5elZLWFhnRzZtSnh4WldBZ0FBcQB+ABE=")?: ItemStack(Material.CLOCK)
                            )
                        }
        )
    }


}