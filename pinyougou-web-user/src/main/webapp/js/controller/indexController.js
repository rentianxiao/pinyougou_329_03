//首页控制器
app.controller('indexController', function ($scope,$controller, loginService ,userService) {

    // AngularJS中的继承:伪继承
    $controller('baseController',{$scope:$scope});

    //显示当前用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    //2019-1-14
    // 分页查询所有订单
    $scope.search = function (page,rows) {
        userService.search(page,rows).success(
            function (response) {
                $scope.paginationConf.totalItems = response.total;
                $scope.list = response.rows;
            }
        );
    }

    $scope.searchEntity={};//定义搜索对象
    //搜索
    $scope.search=function(page,rows){
        userService.search(page,rows,$scope.searchEntity).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    $scope.databyorderId=function (order_id) {
        userService.databyorderId(order_id).success(
            function (response) {
                $scope.userOrderItemVoList = response;
            }
        );
    }
});