//服务层
app.service('orderService',function ($http) {
    //读取列表数据绑定到表单中
    this.findOrderBySellerId=function(){
        return $http.get('../order/findOrderBySellerId.do');
    }

    this.updateStatus = function(ids,status){
        return $http.get('../order/updateStatus.do?ids='+ids+"&status="+status);
    }
    this.selectByStatusAndData = function(status,dateLimit){
        return $http.post('../order/selectByStatusAndData.do?status='+status+"&dateLimit="+dateLimit);
    }
})