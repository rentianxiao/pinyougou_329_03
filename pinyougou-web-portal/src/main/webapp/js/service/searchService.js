app.service('searchService',function($http){
	
	
	this.search=function(searchMap){
		return $http.post('itemsearch/search.do',searchMap);
	}

    this.addToCollection=function(id){
        return $http.get('../itemsearch/addToCollection.do?id='+id);
    }
	
});