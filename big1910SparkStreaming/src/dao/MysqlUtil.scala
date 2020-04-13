package dao

import pojo.TongjiBean
import com.mchange.v2.c3p0.ComboPooledDataSource
import java.sql.Connection
import java.sql.PreparedStatement
import java.text.SimpleDateFormat
import java.sql.ResultSet
/**
 * 通过c3p0连接mysql数据库
 * 建表语句：create table tongji2(reporttime date,pv int,uv int,vv int,newip int,newcust int);
 */
object MysqlUtil {
  val c3p0 = new ComboPooledDataSource
  def saveToMysql(tongjiBean: TongjiBean) = {
    var conn:Connection=null
    var ps1:PreparedStatement=null
    var rs:ResultSet=null
    var ps2:PreparedStatement=null
    var ps3:PreparedStatement=null
    
    try {
      val sdf = new SimpleDateFormat("YYYY-MM-dd")
      val todayTime = sdf.format(tongjiBean.sstime.toLong)
      
      conn = c3p0.getConnection
      ps1 = conn.prepareStatement("select * from tongji2 where reporttime=?")
      ps1.setString(1, todayTime)
      
      // 执行查询,获取结果集
      rs = ps1.executeQuery()
      
      if(rs.next()){
        // 如果当天已经有数据，则做更新累加
        ps2 = conn.prepareStatement("update tongji2 set pv=pv+?,uv=uv+?,vv=vv+?,newip=newip+?,newcust=newcust+? where reporttime=?")
        ps2.setInt(1, tongjiBean.pv)
        ps2.setInt(2, tongjiBean.uv)
        ps2.setInt(3, tongjiBean.vv)
        ps2.setInt(4, tongjiBean.newip)
        ps2.setInt(5, tongjiBean.newcust)
        ps2.setString(6,todayTime)
        
        ps2.executeUpdate()
        
      }else{
        // 如果当天没有数据，则做新增插入
        ps3 = conn.prepareStatement("insert into tongji2 values(?,?,?,?,?,?)")
        ps3.setString(1, todayTime)
        ps3.setInt(2,tongjiBean.pv)
        ps3.setInt(3,tongjiBean.uv)
        ps3.setInt(4,tongjiBean.vv)
        ps3.setInt(5,tongjiBean.newip)
        ps3.setInt(6,tongjiBean.newcust)
        
        ps3.executeUpdate()
        
      }
      
    } catch {
      case t: Throwable => t.printStackTrace() // TODO: handle error
    }finally {
    	if(ps3 !=null) ps3.close()
    	if(ps2 !=null) ps2.close()
      if(ps1 !=null) ps1.close()
      if(rs != null)rs.close()
      if(conn != null) conn.close()
    }
  }
}