app.service("contentService",function($http){
	this.findByCategoryId = function(categoryId){
		return $http.get("content/findByCategoryId.do?categoryId="+categoryId);
	}

    this.findAllItemCat=function(parentId){
        return $http.get('content/findAllItemCat.do?parentId='+parentId);
    }

    this.findBrandName=function () {
        return $http.get('../content/findBrandName.do');
    }
});