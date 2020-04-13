package dao

import org.apache.spark.SparkContext
import pojo.LogBean
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.fs.shell.find.Result
import org.apache.hadoop.hbase.client.Put
import scala.util.Random
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.filter.RowFilter
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp
import org.apache.hadoop.hbase.filter.RegexStringComparator
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Base64
/**
 * 将数据写入到HBase
 */
object HBaseUtil {
  /*写入HBase
   * 行键(sstime_uvid_ssid_cip_随机数字)
   * LogBean(url,urlname,uvid,ssid,sscount, sstime,cip*/
  def saveToHBase(sc: SparkContext, logBean: LogBean) = {
    // 设置zk
    sc.hadoopConfiguration.set("hbase.zookeeper.quorum","10.42.170.88,10.42.150.127,10.42.77.58")
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort","2181")
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE,"weblog")
    
    val job = new Job(sc.hadoopConfiguration)
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    job.setOutputValueClass(classOf[Result])
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
    
    /*行键设计：sstime_uvid_ssid_cip_随机数字
     * 1.时间戳开头，HBase会按时间做升序排序，所以有利于后续的按时间段范围查询
     * 2.用户id、会话id、地址ip，便于后续结合HBase过滤器查询相关的数据
     * 3.随机数满足散列的设计原则*/
    val rowKey = logBean.sstime+"_"+logBean.uvid+"_"+logBean.ssid+"_"+logBean.cip+"_"+Random.nextInt(100)
    // LogBean(url,urlname,uvid,ssid,sscount,sstime,cip)
    val r1 = sc.makeRDD(List(logBean))
    //RDD[LogBean] -> RDD[(输出key,输出value)]  
    val hbaseRDD = r1.map{bean =>
     //创建一个HBase行对象，并设定行键
     val row = new Put(rowKey.getBytes)
     row.add("cf1".getBytes,"url".getBytes,bean.url.getBytes)
     row.add("cf1".getBytes,"urlname".getBytes,bean.urlname.getBytes)
     row.add("cf1".getBytes,"uvid".getBytes,bean.uvid.getBytes)
     row.add("cf1".getBytes,"ssid".getBytes,bean.ssid.getBytes)
     row.add("cf1".getBytes,"cip".getBytes,bean.cip.getBytes)
     (new ImmutableBytesWritable,row)
    }
    
    //执行写出
    hbaseRDD.saveAsNewAPIHadoopDataset(job.getConfiguration)
  }

  /*读取HBase*/
  def queryByRangeAndRegex(sc: SparkContext, startTime: Long, endTime: Long, regex: String) = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum","10.42.170.88,10.42.150.127,10.42.77.58")
    hbaseConf.set("hbase.zookeeper.property.clientPort","2181")
    hbaseConf.set(TableInputFormat.INPUT_TABLE,"weblog")// 指定读取的表名
    
    val scan = new Scan()
    scan.setStartRow(startTime.toString().getBytes)
    scan.setStopRow(endTime.toString().getBytes)
    
    /*过滤器
     * RowFilter(比较规则,正则)*/
    val filter = new RowFilter(CompareOp.EQUAL,new RegexStringComparator(regex))
    // 绑定过滤器到scan对象中
    scan.setFilter(filter)
    // 设置scan对象，使其生效
    hbaseConf.set(TableInputFormat.SCAN,Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))
    // 执行查询，并得到结果集RDD
    val resultRDD = sc.newAPIHadoopRDD(hbaseConf,classOf[TableInputFormat],classOf[ImmutableBytesWritable],classOf[org.apache.hadoop.hbase.client.Result])
    // 返回值(结果集RDD)
    resultRDD
  }
}