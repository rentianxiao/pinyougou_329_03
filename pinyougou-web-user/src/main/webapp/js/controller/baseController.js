app.controller("baseController",function($scope,$controller, loginService){

	// 分页的配置的信息
	$scope.paginationConf = {
		 currentPage: 1, // 当前页数
		 totalItems: 0, // 总记录数
		 itemsPerPage: 5, // 每页显示多少条记录
		 onChange: function(){ // 当页码、每页显示多少条下拉列表发生变化的时候，自动触发了
			$scope.reloadList();// 重新加载列表
		 }
	};
	
	$scope.reloadList= function(){
		// $scope.findByPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
		$scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
	}

	
	// 定义方法：获取JSON字符串中的某个key对应值的集合
	$scope.jsonToString = function(jsonStr,key){
		// 将字符串转成JSOn:[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]  key:"text"
		var jsonObj = JSON.parse(jsonStr);
		
		var value = "";
		for(var i=0;i<jsonObj.length;i++){
			
			if(i>0){
				value += ",";
			}
			
			value += jsonObj[i][key];
		}
		return value;
	}
	
});