//首页控制器
app.controller('addressController', function ($scope,$controller, loginService ,addressService ) {

    //显示当前用户名
    $scope.showName = function () {
        loginService.showName().success(
            function (response) {
                $scope.loginName = response.loginName;
            }
        );
    }

    //获取当前用户的地址列表
    $scope.findAddressList=function(){
        addressService.findAddressList().success(
            function(response){
                $scope.addressList=response;
            }
        );
    }

    // 查询一级分类列表:
    $scope.selectProvincesList = function(){
        addressService.findProvincesList.success(function(response){
            $scope.provincesList = response;
        });
    }

    //需要监听一级分类的列表数据，判断它的数据是否发生改变
    // 查询二级分类列表:
    $scope.$watch("entity.address.provinceId",function(newValue,oldValue){
        addressService.findByProvinceId(newValue).success(function(response){
            $scope.cityList = response;
        });
    });

    //需要监听二级分类的列表数据，判断它的数据是否发生改变
    // 查询三级分类列表:
    $scope.$watch("entity.address.cityId",function(newValue,oldValue){
        addressService.findByCityId(newValue).success(function(response){
            $scope.areasList = response;
        });
    });
})