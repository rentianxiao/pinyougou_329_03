// 定义服务层:
app.service("seckillService",function($http){

	this.add = function(entity){
		return $http.post("../seckill/add.do",entity);
	}
	
	this.update=function(entity){
		return $http.post("../seckill/update.do",entity);
	}
	
	this.findOne=function(id){
		return $http.get("../seckill/findOne.do?id="+id);
	}
	
	this.dele = function(ids){
		return $http.get("../seckill/delete.do?ids="+ids);
	}

	this.search = function(page,rows,searchEntity){
		return $http.post("../seckill/search.do?pageNum="+page+"&pageSize="+rows,searchEntity);
	}
	
	this.selectOptionList = function(){
		return $http.get("../seckill/selectOptionList.do");
	}

    this.findGoodsList = function(){
        return $http.get("../seckill/findAll.do?");
    }
});