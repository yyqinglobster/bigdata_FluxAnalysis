package kafkasource

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.kafka.KafkaUtils
import pojo.LogBean
import dao.HBaseUtil
import java.util.Calendar
import pojo.TongjiBean
import dao.MysqlUtil
/**
 * SparkStreaming消费Kafka中的数据
 */
object Driver {
  def main(args: Array[String]): Unit = {
    /*消费kafka数据，启动的线程数至少是2
     * 一个线程负责SparkStreaming
     * 另一个线程负责消费Kafka数据*/
    val conf = new SparkConf().setMaster("local[5]").setAppName("kafkasource")
    val sc = new SparkContext(conf)
    /*SparkStreaming的批大小设置：
     * 理想状态：上一批次计算完，下一批次正好到达*/
    val ssc = new StreamingContext(sc,Seconds(5))
    val zkHosts = "10.42.170.88:2181,10.42.150.127:2181,10.42.77.58:2181"
    // 定义消费者组(组内竞争，组间共享)
    val groupId = "1910kafka"
    
    // 定义消费的主题名和消费的线程数；例 Map("weblog"->1,"topic2"->2)
    val topics = Map("weblog"->2)
    
    // 利用kafkaUtils工具类消费数据;返回值是元组(null,data)
    val kafkaSource = KafkaUtils.createStream(ssc, zkHosts, groupId, topics)
                                .map{x=>x._2}
//    kafkaSource.print()
    /*http://localhost:8080/FluxAppServer/a.jsp|
     * a.jsp|
     * 页面A|
     * UTF-8|
     * 972x648|
     * 24-bit|
     * zh-cn|
     * 0|
     * 1|
     * |
     * 0.9792018372956491|
     * |
     * Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:75.0) Gecko/20100101 Firefox/75.0|
     * 30401487662826346394|
     * 9511629496_4_1586761921182|
     * 127.0.0.1*/
    
    /*对埋点数据实时处理
     * foreachRDD 将DStream转变为RDD进行操作	
     * RDD存储了当前批次中所有的数据(一条一条的数据)*/
    kafkaSource.foreachRDD{rdd => 
      // 转变为迭代器
      val lines = rdd.toLocalIterator
      // 遍历迭代器
      while(lines.hasNext){
        // 获取一条数据
        val line = lines.next()
        // 第一步：做字段清洗。本项目中需要的字段:url,urlname,uvid,ssid,sscount,sstime,cip
        val info = line.split("\\|")
        val url = info(0)
        val urlname = info(1)
        val uvid = info(13)
        val ssid = info(14).split("_")(0)
        val sscount = info(14).split("_")(1)
        val sstime = info(14).split("_")(2)
        val cip = info(15)
        // 第二步：将业务字段封装到指定的bean中
        val logBean = LogBean(url,urlname,uvid,ssid,sscount,sstime,cip)
        println(logBean)
        
        // 第三步：统计业务指标，实时计算的业务指标 ：①pv用户访问数 ②uv独立访客数 ③vv独立会话数 ④newip新增ip数 ⑤newcust新增 用户数
        /*	3.1  统计pv：用户访问一次页面则pv=1	
         *  3.2 统计uv: 独立访客数。uv=1或0.判断依据：
         *  			如果uvid当天记录大于1条，则uv=0;
         *  			如果uvid当天记录等于1条，则uv=1;
         *  		难点：startTIme=当天0:00，endTime=sstime
         *  		查询范围定义好后，根据当前uvid去HBase表查询是否出现过--使用正则表达式匹配
         *  3.3 统计vv:独立会话数。vv=0或1。判断依据：
         *  			如果ssid大于1条，则vv=0出现过;
         *  			如果ssid等于1条，则vv=1没出现过;
         *  3.4 统计newip:新增ip数。newip=1 or newip=0 判断依据：
         *  			如果cip在历史数据中没出现过，则newip=1;
         *  			如果cip在历史数据中出现过，则newip=0;
         *  3.5 统计newcust:新增用户数。
         *  */
        /*3.2 pv用户访问数*/
        val pv = 1
        /*3.2 uv独立访客数*/
        val endTime = sstime.toLong //终止时间戳
        val calendar = Calendar.getInstance
        calendar.setTimeInMillis(endTime)
        calendar.set(Calendar.HOUR,0)
        calendar.set(Calendar.MINUTE,0)
        calendar.set(Calendar.SECOND,0)
        calendar.set(Calendar.MILLISECOND,0)
        val startTime = calendar.getTimeInMillis//开始时间戳
        //uvid在HBase表查询是否出现过
        /*行键(sstime_uvid_ssid_cip_随机数字)
         * 1586762638710_37446226323043635882_5772220112_0:0:0:0:0:0:0:1_75
         *     sstime           uvid              ssid         cip       random */
        val uvRegex = "^\\d+_"+uvid+".*$"
        val uvResultRDD = HBaseUtil.queryByRangeAndRegex(sc,startTime,endTime,uvRegex)
        val uv = if(uvResultRDD.count()==0) 1 else 0
//        println("pv:"+pv+",uv:"+uv)
        
        /*3.3 vv独立会话数*/
        val vvRegex = "^\\d+_\\d+_"+ssid+".*$"
        val vvResultRDD = HBaseUtil.queryByRangeAndRegex(sc, startTime, endTime, vvRegex)
        val vv = if(vvResultRDD.count()==0) 1 else 0
        println("pv:"+pv+",uv:"+uv+",vv:"+vv)
        
        /*3.4 统计newip新增ip数*/
        val newipRegex = "^\\d+_\\d+_\\d+_"+cip+".*$"
        val newipResultRDD=HBaseUtil.queryByRangeAndRegex(sc, 0, endTime, newipRegex)
        val newip = if(newipResultRDD.count()==0) 1 else 0
        
        /*3.5 统计newcust新增用户数*/
        val newcustRegex = "^\\d+_"+uvid+".*$"// 同uvRegex，可不写
        val newcustResultRDD=HBaseUtil.queryByRangeAndRegex(sc, 0, endTime, newcustRegex)
        val newcust = if(newcustResultRDD.count()==0) 1 else 0
        
        // 第四步：将统计出的业务指标封装到bean,并写出到mysql
        val tongjiBean = TongjiBean(sstime,pv,uv,vv,newip,newcust)
        MysqlUtil.saveToMysql(tongjiBean)
        
        // 第五步：将logBean数据存储到HBase指定的表中
        HBaseUtil.saveToHBase(sc,logBean)
      }
    }
   
    ssc.start()
    ssc.awaitTermination()
    
  }
}