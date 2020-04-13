package sparkhbase

import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat
import org.apache.hadoop.mapreduce.Job
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.fs.shell.find.Result
import org.apache.hadoop.hbase.client.Put
/**
 * 将Spark和HBase整合
 * 将数据写出到指定的HBase表
 */
object WriteDriver {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[3]").setAppName("writeHBase")
    val sc = new SparkContext(conf)
    // 设置zookeeper
    sc.hadoopConfiguration.set("hbase.zookeeper.quorum","10.42.170.88,10.42.150.127,10.42.77.58")
    // 设置zookeeper通信端口
    sc.hadoopConfiguration.set("hbase.zookeeper.property.clientPort","2181")
    // 设置输出的表名
    sc.hadoopConfiguration.set(TableOutputFormat.OUTPUT_TABLE,"tbc")
    
    // import org.apache.hadoop.mapreduce.Job
    val job = new Job(sc.hadoopConfiguration)
    // 输出key类型:定长序列化的字节
    job.setOutputKeyClass(classOf[ImmutableBytesWritable])
    // 输出value类型:Result
    job.setOutputValueClass(classOf[Result])//import org.apache.hadoop.fs.shell.find.Result
    // 输出的表类型:ImmutableBytesWritable
    job.setOutputFormatClass(classOf[TableOutputFormat[ImmutableBytesWritable]])
    
    // 准备测试数据
    val r1 = sc.makeRDD(List("1 tom 23","2 rose 18","3 jim 25","4 jary 30"))
    
    /*为了能够将指定的RDD数据插入到HBase表，需要转成对应的RDD[(输出key,输出value)]*/
    val hbaseRDD = r1.map{ line =>
      val info = line.split(" ")
      val id = info(0)
      val name = info(1)
      val age = info(2)
      // 创建HBase的行对象，并指定行键
      val row = new Put(id.getBytes)
      // add(列族名,列名,列值)
      row.add("cf1".getBytes,"name".getBytes,name.getBytes)
      row.add("cf1".getBytes,"age".getBytes,age.getBytes)
      
      (new ImmutableBytesWritable,row)
    }
    hbaseRDD.saveAsNewAPIHadoopDataset(job.getConfiguration)
    
  }
  
}