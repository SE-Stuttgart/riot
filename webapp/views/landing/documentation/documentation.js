angular.module('riot').config(function($stateProvider) {
  $stateProvider.state('landing.documentation', {
    url: '/documentation',
    templateUrl: 'views/landing/documentation/documentation.html'
  });
});

angular.module('riot').controller('DocumentationCtrl', function($scope) {
	
	$scope.navLinks = [{
        title: 'REST API',
        link: 'documentation/rest_api_operations.md',
    }, {
        title: 'Clientlibrary',
        link: 'documentation/clientlibrary.md'
    }, {
        title: 'Authentication and Authorization',
        link: 'documentation/authentication_authorization.md'
    }, {
        title: 'Technology',
        link: 'documentation/technology.md'
    }, {
        title: 'Database',
        link: 'documentation/database.md'
    }, {
        title: 'Concept',
        link: 'documentation/concept.md'
    }, {
        title: 'Creating-Rules',
        link: 'documentation/Creating-Rules.md'
    }, {
        title: 'Managing-Rules',
        link: 'documentation/Managing-Rules.md'
    }, {
        title: 'Scenarios',
        link: 'documentation/scenario.md'
    }];
	
	$scope.markdownDocument = window.sessionStorage.getItem("markdownDocument") || "documentation/rest_api_operations.md";
	
	$scope.changeMarkdownDocument = function(link){
		$scope.markdownDocument = link;
		window.sessionStorage.setItem("markdownDocument", link);
	};
  
	$scope.navClass = function (link) {
        return link === $scope.markdownDocument ? 'active' : '';
    };
	
	$scope.contendLoaded = function() {
		var links = document.getElementById("markdownDiv").getElementsByTagName("a");
		var onclick = function() {
			var doc = this.getAttribute("href");
			console.log("change Dokument to documentation/" + doc);
			$scope.changeMarkdownDocument("documentation/" + doc);
			$scope.$apply();
			return false;
		};
		for(var i=0;i<links.length;i++) {
			var href = links[i].getAttribute("href");
			if (href.indexOf("://") === -1) {
				if (href.lastIndexOf(".md") === href.length-3) {
					var doc = href.substr(href.lastIndexOf('/') + 1);
					links[i].href=doc;
					links[i].onclick=onclick;
				} else {
					links[i].href = "documentation/" + href.substr(href.lastIndexOf('/') + 1);
				}
			}
		}
		links = document.getElementById("markdownDiv").getElementsByTagName("img");
		for(i=0;i<links.length;i++) {
			var src = links[i].getAttribute("src");
			if (src.indexOf("://") === -1) {
				links[i].src = "documentation/" + src.substr(src.lastIndexOf('/') + 1);
			}
		}
	};

});