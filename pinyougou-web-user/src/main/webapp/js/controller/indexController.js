//首页控制器
app.controller('indexController', function ($scope, loginService) {
    //显示当前用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    $scope.findAll = function () {
        orderService.findAll().success(
            function () {

            }
        );
    }
});