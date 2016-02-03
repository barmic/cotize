var cotizeControllers = angular.module('cotizeControllers', []);

cotizeControllers.controller('cotizeCreateProject', ['$http', '$scope', 'cotizeProjectService',
function ($http, $scope, cotizeProjectService) {
    // App session information
    $scope.project = {
        name : '',
        author : '',
        mail : '',
        description : ''
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2]
    };

    $scope.project.create = function () {
        cotizeProjectService.createProject($scope.project)
            .success(function (data) {
                $scope.project.state = "created";
                $scope.project.content = data;
            })
            .error(function (data, status) {
                $scope.project.state = "error";
                $scope.project.status = status;
            });
    };

}]);

cotizeControllers.controller('cotizeProject', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.contribution = {
        "author": "",
        "mail": "",
        "amount": "",
        "projectId": $routeParams.projectId
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.newcontrib = {}

    cotizeProjectService.loadProject($routeParams.projectId)
            .success(function (data) {
                $scope.project.content = data;
            })
            .error(function (data, status) {
            });

    $scope.create.contribution = function () {
        cotizeProjectService.contribute($routeParams.projectId, $scope.contribution)
            .success(function (data) {
                $scope.newcontrib.state = "created";
                $scope.newcontrib.content = data.deals.filter(d => d.creditor === $scope.contribution.author)[0];
                $scope.project.content = data;
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);

cotizeControllers.controller('cotizeContribution', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.contribution = {
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.newcontrib = {}

    cotizeProjectService.loadContribution($routeParams.projectId, $routeParams.contributionId)
            .success(function (data) {
                $scope.project.content = data;
                $scope.contribution = data.deals[0];
            })
            .error(function (data, status) {
            });

    $scope.contribution.update = function () {
        cotizeProjectService.updateContribution($routeParams.projectId, $routeParams.contributionId, $scope.contribution)
            .success(function (data) {
                $scope.newcontrib.state = "created";
                $scope.newcontrib.content = data.deals.filter(d => d.creditor === $scope.contribution.author)[0];
                $scope.project.content = data;
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);