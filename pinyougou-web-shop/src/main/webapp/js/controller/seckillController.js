// 定义控制器:
app.controller("seckillController",function($scope,$controller,seckillService){
	// AngularJS中的继承:伪继承
	$controller('baseController',{$scope:$scope});


	// 分页查询
	$scope.findPage = function(page,rows){
		// 向后台发送请求获取数据:
        seckillService.findPage(page,rows).success(function(response){
			$scope.paginationConf.totalItems = response.total;
			$scope.list = response.rows;
		});
	}
	
	// 保存品牌的方法:
	$scope.save = function(){
		// 区分是保存还是修改
		var object;
		if($scope.entity.id != null){
			// 更新
			object = seckillService.update($scope.entity);
		}else{
			// 保存
			object = seckillService.add($scope.entity);
		}
		object.success(function(response){
			// {flag:true,message:xxx}
			// 判断保存是否成功:
			if(response.flag){
				// 保存成功
				alert(response.message);
				$scope.reloadList();
			}else{
				// 保存失败
				alert(response.message);
			}
		});
	}
	
	// 查询一个:
	$scope.findById = function(id){
        seckillService.findOne(id).success(function(response){
			// {id:xx,name:yy,firstChar:zz}
			$scope.entity = response;
		});
	}
	
	// 删除品牌:
	$scope.dele = function(){
        seckillService.dele($scope.selectIds).success(function(response){
			// 判断保存是否成功:
			if(response.flag==true){
				// 保存成功
				// alert(response.message);
				$scope.reloadList();
				$scope.selectIds = [];
			}else{
				// 保存失败
				alert(response.message);
			}
		});
	}

	$scope.status = ["未审核","审核通过","已驳回","商品关闭"]
    $scope.findGoodsList = function(){
        seckillService.findGoodsList().success(function(response){
            // {id:xx,name:yy,firstChar:zz}
            $scope.goodsList = response;

        });
    }
	
	$scope.searchEntity={};
	
	// 假设定义一个查询的实体：searchEntity
	$scope.search = function(page,rows){
		// 向后台发送请求获取数据:
        seckillService.search(page,rows,$scope.searchEntity).success(function(response){
			$scope.paginationConf.totalItems = response.total;
			$scope.list = response.rows;
		});
	}
	
});
