 //控制层 
app.controller('userController' ,function($scope,$controller ,loginService ,userService,uploadService){

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

    //显示当前用户名
    $scope.findUser = function () {
        userService.findUser().success(
            function (response) {
                $scope.userList = response;
            }
        );
    }

    // 文件上传的方法:
    $scope.uploadFile = function(){
        uploadService.uploadFile().success(function(response){
            if(response.flag){
                $scope.entity.pic = response.message;
            }else{
                alert(response.message);
            }
        });
    }
});	
