package streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.Milliseconds
import org.apache.spark.SparkContext

object Driver {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local[2]").setAppName("streaming")
    val sc = new SparkContext(conf)
    val ssc = new StreamingContext(sc,Milliseconds(3000))//3秒
    
    // 从hdfs获取数据源
    val dataSource = ssc.textFileStream("hdfs://hadoop01:9000/data")
    
    dataSource.print()
    
    ssc.start()
    
    // 保持SparkStreaming一直开启，直到用户主动终止退出
    ssc.awaitTermination()
  }
}