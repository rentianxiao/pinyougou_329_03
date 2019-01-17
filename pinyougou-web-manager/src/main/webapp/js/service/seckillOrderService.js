// 定义服务层:
app.service("seckillOrderService",function($http){
	this.findAll = function(){
		return $http.get("../seckillOrder/findAll.do");
	}
	
	this.findPage = function(page,rows){
		return $http.get("../seckillOrder/findPage.do?pageNum="+page+"&pageSize="+rows);
	}
	
	this.add = function(entity){
		return $http.post("../seckillOrder/add.do",entity);
	}
	
	this.update=function(entity){
		return $http.post("../seckillOrder/update.do",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../seckillOrder/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../seckillOrder/delete.do?ids="+ids);
	}
	
	this.search = function(page,rows,searchEntity){
		return $http.post("../seckillOrder/search.do?pageNum="+page+"&pageSize="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../seckillOrder/selectOptionList.do");
	}
});