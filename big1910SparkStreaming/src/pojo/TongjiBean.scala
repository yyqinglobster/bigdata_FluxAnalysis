package pojo
/**
 * 统计结果bean
 * sstime 	时间戳
 * pv 			用户访问数 
 * uv				独立访客数 
 * vv				独立会话数 
 * newip		新增ip数 
 * newcust	新增 用户数
 */
case class TongjiBean(val sstime:String,
                      val pv:Int,
                      val uv:Int,
                      val vv:Int,
                      val newip:Int,
                      val newcust:Int) {
  
}