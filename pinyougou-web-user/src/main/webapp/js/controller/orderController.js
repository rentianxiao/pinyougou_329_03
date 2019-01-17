// 定义控制器:
app.controller("orderController",function($scope,$controller,userService){
    // AngularJS中的继承:伪继承
    $controller('baseController',{$scope:$scope});

    //2019-1-14
    // 分页查询所有订单
    $scope.findPage = function (page,rows) {
        userService.findPage(page,rows).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.orderlist = response.rows;
            }
        );
    }
});