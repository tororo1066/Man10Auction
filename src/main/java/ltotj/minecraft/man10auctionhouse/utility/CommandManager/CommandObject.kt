package ltotj.minecraft.man10auctionhouse.utility.CommandManager

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import java.lang.StringBuilder
import java.util.function.Consumer
import kotlin.collections.ArrayList

class CommandObject{

    private var argNum = 1
    private val nextObjects = ArrayList<CommandObject>()
    private var nullableNextArgument = false
    private var function:Consumer<Pair<CommandSender,Array<out String>>>?=null
    private var errorMessage=arrayOf("§4エラーメッセージ未設定")

    private val allowedType= ArrayList<CommandArgumentType>()
    private var argument:String?=null
    private var comment:String?=null
    private var hide=false
    private var permission:String?=null
    private var explanation=""

    constructor(argument:String){
        this.argument=argument
    }

    constructor(argumentType: CommandArgumentType){
        allowedType.add(argumentType)
    }

    constructor(argumentTypes: Array<CommandArgumentType>){
        allowedType.addAll(argumentTypes)
    }

    private fun getTypeString(commandArgumentType: CommandArgumentType):String{
        return when(commandArgumentType){
            CommandArgumentType.INT,CommandArgumentType.Long -> "整数"
            CommandArgumentType.BOOLEAN -> "true/false"
            CommandArgumentType.STRING -> "文字列"
            CommandArgumentType.ONLINE_PlAYER -> "プレイヤー名"
            CommandArgumentType.DOUBLE -> "数字"
        }
    }

    fun hide():Boolean{
        return hide
    }

    fun addAllowedArgType(commandArgumentType: CommandArgumentType): CommandObject {
        allowedType.add(commandArgumentType)
        comment+="$commandArgumentType"
        return this
    }

    fun addNextArgument(commandObject: CommandObject): CommandObject {
        nextObjects.add(commandObject)
        commandObject.argNum = argNum + 1
        return this
    }

    fun hasPermission(sender:CommandSender):Boolean{
        return if(permission==null) true else sender.hasPermission(permission!!)
    }

    fun getArgument():MutableList<String>{
        val list= mutableListOf<String>()
        if(hide){
            return list
        }
        if(comment!=null){
            list.add(comment!!)
        }
        else if(argument!=null){
            list.add(argument!!)
        }
        else{
            if(allowedType.contains(CommandArgumentType.INT))list.add("整数")
            if(allowedType.contains(CommandArgumentType.BOOLEAN)){
                list.add("true")
                list.add("false")
            }
            if(allowedType.contains(CommandArgumentType.DOUBLE))list.add("数字")
            if(allowedType.contains(CommandArgumentType.STRING))list.add("文字列")
            if(allowedType.contains(CommandArgumentType.ONLINE_PlAYER)){
                for(player in Bukkit.getOnlinePlayers()){
                    list.add(player.name)
                }
            }
        }
        return list
    }

    private fun getNextArguments():MutableList<String> {
        val list = mutableListOf<String>()
        for (next in nextObjects) {
            if(!next.hide) {
                if (next.comment != null) {
                    list.add(comment!!)
                } else {
                    for (type in next.allowedType) {
                        list.add(getTypeString(type))
                    }
                    list.add(next.argument ?: continue)
                }
            }
        }
        return list
    }

    fun getExplanation():String{
        val message=StringBuilder()

        message.append(" §e${if(argument!=null) argument else getArgument().toString()}")
        if(nextObjects.size<2){
            return if(nextObjects.isEmpty()){
                message.append(" §d-> $explanation")
                message.toString()
            } else{
                message.append(nextObjects[0].getExplanation())
                message.toString()
            }
        }
        return message.append(" ${getNextArguments()} ... §d-> §e$explanation").toString()
    }

    fun match(string: String):Boolean{
        if(argument==null) {
            if (allowedType.contains(CommandArgumentType.STRING)) return true
            if (allowedType.contains(CommandArgumentType.DOUBLE) && string.toDoubleOrNull() != null) return true
            if (allowedType.contains(CommandArgumentType.INT) && string.toIntOrNull() != null) return true
            if (allowedType.contains(CommandArgumentType.Long)&&string.toLongOrNull()!=null) return true
            if (allowedType.contains(CommandArgumentType.BOOLEAN) && (string == "true" || string == "false")) return true
            if (allowedType.contains(CommandArgumentType.ONLINE_PlAYER)) {
                return Bukkit.getPlayer(string) != null
            }
            return false
        }
        else return argument==string
    }

    fun setArgument(string: String): CommandObject {
        argument=string
        return this
    }

    fun setComment(string: String): CommandObject {
        comment="<${string}>"
        return this
    }

    fun setHidden(boolean: Boolean): CommandObject {
        hide=boolean
        return this
    }

    fun setPermission(permission:String): CommandObject {
        this.permission=permission
        return this
    }

    fun setNullable(boolean: Boolean): CommandObject {
        nullableNextArgument = boolean
        return this
    }

    fun setExplanation(string: String): CommandObject {
        explanation="§d${string}"
        return this
    }

    fun setErrorMessage(string:String): CommandObject {
        errorMessage= arrayOf(string)
        return this
    }

    fun setErrorMessage(array:Array<String>): CommandObject {
        errorMessage=array
        return this
    }

    fun setFunction(consumer: Consumer<Pair<CommandSender,Array<out String>>>): CommandObject {
        function=consumer
        return this
    }

    fun getTabComplete(sender: CommandSender,args:Array<String>):MutableList<String>{
        val list = mutableListOf<String>()
        if(args.size-1==argNum){
            for (commandObject in nextObjects) {
                if(commandObject.hasPermission(sender))list.addAll(commandObject.getArgument())
            }
        }
        else{
            for(commandObject in nextObjects){
                if(commandObject.match(args[argNum])){
                    if(commandObject.hasPermission(sender))list.addAll(commandObject.getTabComplete(sender,args))
                }
            }
        }
        return list
    }

    fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size == argNum) {
            if(nullableNextArgument||nextObjects.isEmpty()) {
                if (function == null) {
                    sender.sendMessage("§4[エラー]コマンドの処理が実装されていません")
                    return
                }
                else{
                    function!!.accept(Pair(sender,args))
                    return
                }
            }
            else{
                sender.sendMessage(errorMessage)
            }
        }else{
            for(commandObject in nextObjects){
                if(commandObject.match(args[argNum])){
                    commandObject.execute(sender,args)
                    return
                }
            }
            sender.sendMessage(errorMessage)
        }
    }
}