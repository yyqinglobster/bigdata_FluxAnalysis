<%@ page language="java" contentType="text/html; charset=utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<title>WebLog DataView</title>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/echarts.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.js"></script>
<script type="text/javascript">
	$(function(){
		// 初始化echarts实例
		var myChart = echarts.init(document.getElementById('main'));
		pvArr=[]
		uvArr=[]
		vvArr=[]
		newipArr=[]
		newcustArr=[]
		
		// 定时刷新
		window.setInterval(function(){
			$.get("${pageContext.request.contextPath}/DataView",function(datax){
				
				if(pvArr.length>7){
					pvArr.shift()
					uvArr.shift()
					vvArr.shift()
					newipArr.shift()
					newcustArr.shift()
				}
				
				pvArr.push(eval(datax)[0])
				uvArr.push(eval(datax)[1])
				vvArr.push(eval(datax)[2])
				newipArr.push(eval(datax)[3])
				newcustArr.push(eval(datax)[4])
				
				// 平滑折线图
				var option = {
						legend: {
							data:['pv','uv','vv','newip','newcust']
						},
					    xAxis: {
					        type: 'category',
					        data: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
					    },
					    yAxis: {
					        type: 'value'
					    },
					    series: [{
					    	name:'pv',
					        data: pvArr,
					        type: 'line',
					        smooth: true
					    },{
					    	name:'uv',
					        data: uvArr,
					        type: 'line',
					        smooth: true
					    },{
					    	name:'vv',
					        data: vvArr,
					        type: 'line',
					        smooth: true
					    },{
					    	name:'newip',
					        data: newipArr,
					        type: 'line',
					        smooth: true
					    },{
					    	name:'newcust',
					        data: newcustArr,
					        type: 'line',
					        smooth: true
					    }]
					};
				
				// 使用刚指定的配置项和数据显示图表
				myChart.setOption(option);	
				
			});
		},2000);
		
		
	});

</script>
</head>
<body>
	<div id="main" style="width:800px;height:400px"></div>

</body>
</html>