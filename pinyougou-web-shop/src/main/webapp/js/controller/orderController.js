app.controller('orderController',function ($scope,$controller   ,orderService) {
    $controller('baseController',{$scope:$scope});//继承

    $scope.status = [" ","未付款","已付款","未发货","已发货","交易成功","交易关闭","待评价"];
    $scope.sourceType=["app端","pc端","M端","微信端","手机qq端"];
    $scope.payment=["","在线支付","货到付款"];
    //读取列表数据绑定到表单中
    $scope.findOrderBySellerId=function(){
        orderService.findOrderBySellerId().success(
            function(response){
                $scope.list=response;
            }
        );
    }

    // 发货的方法:
    $scope.updateStatus = function(status){
        orderService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadListWith();//刷新列表
                $scope.selectIds = [];
                alert(response.message);
            }else{
                alert(response.message);
            }
        });
    }


    //搜索
    $scope.selectByStatusAndData=function(status,dateLimit){
        orderService.selectByStatusAndData(status,dateLimit).success(
            function(response){
                $scope.list=response;
                // $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }
})