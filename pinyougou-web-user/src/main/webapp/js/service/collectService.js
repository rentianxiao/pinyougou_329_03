//服务层
app.service('collectService',function($http){

    //查询所有
    this.findAll=function(){
        return $http.get('../collect/findAll.do');
    }
});