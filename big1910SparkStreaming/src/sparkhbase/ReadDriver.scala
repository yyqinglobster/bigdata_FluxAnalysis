package sparkhbase

import org.apache.spark.SparkContext
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.spark.SparkConf
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.hbase.client.Result
import org.apache.hadoop.hbase.client.Scan
import org.apache.hadoop.hbase.protobuf.ProtobufUtil
import org.apache.hadoop.hbase.util.Base64
/**
 * 从HBase读取数据
 */
object ReadDriver {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[5]").setAppName("readHBase")
    val sc = new SparkContext(conf)
    val hbaseConf = HBaseConfiguration.create()
    
    hbaseConf.set("hbase.zookeeper.quorum","10.42.170.88,10.42.150.127,10.42.77.58")
    hbaseConf.set("hbase.zookeeper.property.clientPort","2181")
    hbaseConf.set(TableInputFormat.INPUT_TABLE,"tbc")
    
    /*按行键范围查询*/
    val scan = new Scan()
    // 起始行键
    scan.setStartRow("1".getBytes)
    // 终止行键
    scan.setStopRow("3".getBytes)//左闭右开，不含3
    // 将scan对象设置到HBaseConf参数中
    hbaseConf.set(TableInputFormat.SCAN,Base64.encodeBytes(ProtobufUtil.toScan(scan).toByteArray()))
    
    
    // 执行读取，返回RDD
    val resultRDD = sc.newAPIHadoopRDD(hbaseConf, classOf[TableInputFormat], classOf[ImmutableBytesWritable], classOf[Result])
    resultRDD.foreach{case(key,row) =>
      val name = row.getValue("cf1".getBytes, "name".getBytes)
      val age = row.getValue("cf1".getBytes, "age".getBytes)
      println(new String(name)+":"+new String(age))
    }
  }
  
}