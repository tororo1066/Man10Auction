package ltotj.minecraft.man10auctionhouse.command

import ltotj.minecraft.man10auctionhouse.Main
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandArgumentType
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandManager
import ltotj.minecraft.man10auctionhouse.utility.CommandManager.CommandObject
import ltotj.minecraft.man10auctionhouse.utility.ValutManager.VaultManager
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class BiddingCommand: CommandManager(Main.plugin,"mbid", Main.pluginTitle) {

    private val bidder=HashMap<UUID,Long>()
    private val vault=VaultManager(Main.plugin)

    init {
        addFirstArgument(CommandObject(CommandArgumentType.Long)
                .setErrorMessage("${Main.pluginTitle}§4入札額を整数で指定してください")
                .setComment("入札金額")
                .setExplanation("目玉商品を指定した額で入札します")
                .setFunction{
                    val num=it.second[0].toLongOrNull()?:return@setFunction
                    val sender=it.first
                    if(sender !is Player){
                        sender.sendMessage("${Main.pluginTitle}§4このコマンドはプレイヤーのみが実行できます")
                        return@setFunction
                    }
                    if(Main.spAuction==null){
                        sender.sendMessage("${Main.pluginTitle}§4現在オークションは行われていません")
                        return@setFunction
                    }
                    if(!vault.hasMoney(sender,num.toDouble())){
                        sender.sendMessage("${Main.pluginTitle}§4所持金が不足しています")
                        return@setFunction
                    }
                    val data=Main.spAuction!!.getData()
                    if(data==null){
                        sender.sendMessage("${Main.pluginTitle}§4オークションの情報を取得できませんでした")
                        return@setFunction
                    }
                    if(bidder.containsKey(sender.uniqueId)&&bidder[sender.uniqueId]!=num){
                        sender.sendMessage("${Main.pluginTitle}§4前回の入力金額と異なります")
                        sender.sendMessage("${Main.pluginTitle}§4入力は無効となりました")
                        bidder.remove(sender.uniqueId)
                        return@setFunction
                    }
                    if(num%data.unit.toLong()!=0L){
                        sender.sendMessage("${Main.pluginTitle}§4一口当たりの金額の倍数を入力してください")
                        sender.sendMessage("${Main.pluginTitle}§a今回の商品の一口当たりの金額は§e${data.unit}円です")
                        return@setFunction
                    }
                    if(!bidder.containsKey(sender.uniqueId)){
                        sender.sendMessage("${Main.pluginTitle}§e確認のためにもう一度同じコマンドを実行してください")
                        sender.sendMessage("${Main.pluginTitle}§8入力情報 §7入札価格：${getYenString(num.toString())}")
                        bidder[sender.uniqueId]=num
                        return@setFunction
                    }
                    if(bidder.containsKey(sender.uniqueId)&&bidder[sender.uniqueId]==num){
                        //入札処理
                        Main.spAuction?.bid(sender,num/data.unit)
                    }
                    sender.sendMessage("§4エラー")
                }
        )
                .addFirstArgument(CommandObject("general")
                        .addNextArgument(CommandObject(CommandArgumentType.INT)
                                .setComment("id")
                                .addNextArgument(CommandObject(CommandArgumentType.Long)
                                        .setExplanation("一般の商品を、IDを指定して入札できます")
                                        .setComment("入札金額")
                                        .setFunction{
                                            val sender=it.first
                                            val id=it.second[1].toIntOrNull()?:return@setFunction
                                            val num= it.second[2].toLongOrNull() ?:return@setFunction
                                            if(sender !is Player){
                                                sender.sendMessage("${Main.pluginTitle}§4このコマンドはプレイヤーのみが実行できます")
                                                return@setFunction
                                            }
                                            if(Main.gnAuction==null){
                                                sender.sendMessage("${Main.pluginTitle}§4現在オークションは行われていません")
                                                return@setFunction
                                            }
                                            if(!vault.hasMoney(sender,num.toDouble())){
                                                sender.sendMessage("${Main.pluginTitle}§4所持金が不足しています")
                                                return@setFunction
                                            }
                                            val data=Main.gnAuction!!.getData(id)
                                            if(data==null){
                                                sender.sendMessage("${Main.pluginTitle}§4オークションの情報を取得できませんでした")
                                                return@setFunction
                                            }
                                            if(bidder.containsKey(sender.uniqueId)&&bidder[sender.uniqueId]!=num){
                                                sender.sendMessage("${Main.pluginTitle}§4前回の入力金額と異なります")
                                                sender.sendMessage("${Main.pluginTitle}§4入力は無効となりました")
                                                bidder.remove(sender.uniqueId)
                                                return@setFunction
                                            }
                                            if(num%data.unit.toLong()!=0L){
                                                sender.sendMessage("${Main.pluginTitle}§4一口当たりの金額の倍数を入力してください")
                                                sender.sendMessage("${Main.pluginTitle}§a今回の商品の一口当たりの金額は§e${data.unit}円です")
                                                return@setFunction
                                            }
                                            if(!bidder.containsKey(sender.uniqueId)){
                                                sender.sendMessage("${Main.pluginTitle}§e確認のためにもう一度同じコマンドを実行してください")
                                                sender.sendMessage("${Main.pluginTitle}§8入力情報 §7入札価格：${getYenString(num.toString())}")
                                                bidder[sender.uniqueId]=num
                                                return@setFunction
                                            }
                                            if(bidder.containsKey(sender.uniqueId)&&bidder[sender.uniqueId]==num){
                                                //入札処理

                                            }
                                            sender.sendMessage("§4エラー")
                                        })))
    }

    private fun getYenString(money: String): String {
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
}