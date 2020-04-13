package pojo
/**
 * 封装bean，一般使用样例类case class
 *  特点：
 *  	1.必须声明主构造器
 *  	2.当主构造器声明后，会自动隐式生成一个空的构造器
 *  	3.混入序列化特质Serializable
 *  	4.默认实现toString()
 *  	5.不需要new就可以创建对象
 */
case class LogBean(val url:String,
    val urlname:String,
    val uvid:String,
    val ssid:String,
    val sscount:String,
    val sstime:String,
    val cip:String){
}