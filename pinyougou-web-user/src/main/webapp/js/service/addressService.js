//服务层
app.service('addressService',function($http){

    //获取当前登录账号的收货地址
    this.findAddressList=function(){
        return $http.post('../address/findListByLoginUser.do');
    }

    //获取地区
    this.findByParentId = function(parentId){
        return $http.get("../address/findByParentId.do?parentId="+parentId);
    }

});