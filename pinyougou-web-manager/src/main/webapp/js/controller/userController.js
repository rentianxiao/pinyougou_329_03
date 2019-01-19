app.controller('userController' ,function($scope,$controller,userService){

    // AngularJS中的继承:伪继承
    $controller('baseController',{$scope:$scope});

    $scope.entity = {};
    //注册用户
    $scope.reg=function(){

        //比较两次输入的密码是否一致
        if($scope.password!=$scope.entity.password){
            alert("两次输入密码不一致，请重新输入");
            $scope.entity.password="";
            $scope.password="";
            return ;
        }
        //新增
        userService.add($scope.entity,$scope.smscode).success(
            function(response){
                alert(response.message);
            }
        );
    }

    //发送验证码
    $scope.sendCode=function(){
        if($scope.entity.phone==null || $scope.entity.phone==""){
            alert("请填写手机号码");
            return ;
        }

        userService.sendCode($scope.entity.phone  ).success(
            function(response){
                alert(response.message);
            }
        );
    }

    $scope.searchEntity={};

    $scope.search = function(page,rows){
        // 向后台发送请求获取数据:
        userService.search(page,rows,$scope.searchEntity).success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }
    $scope.status = ["未冻结","已冻结"];
    // 审核的方法:
    $scope.updateStatus = function(status){
        userService.updateStatus($scope.selectIds,status).success(function(response){
            if(response.flag){
                $scope.reloadList();//刷新列表
                $scope.selectIds = [];
            }else{
                alert(response.message);
            }
        });
    }
/*
*    用户活跃
* */
    $scope.sz = function(){
        // 向后台发送请求获取数据:
        userCountService.sz().success(function(response){
            $scope.paginationConf.totalItems = response.total;
            $scope.list = response.rows;
        });
    }

});
