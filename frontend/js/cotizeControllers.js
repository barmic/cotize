//  Licence Public Barmic
//  copyright 2014-2016 Michel Barret <michel.barret@gmail.com>
var cotizeControllers = angular.module('cotizeControllers', []);

cotizeControllers.controller('cotizeCreateProject', ['$http', '$scope', 'cotizeProjectService',
function ($http, $scope, cotizeProjectService) {
    // App session information
    $scope.project = {};
    $scope.newproject = {};
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };

    $scope.project.create = function () {
        cotizeProjectService.createProject($scope.newproject)
            .success(function (data) {
                $scope.project.state = "created";
                $scope.project.content = data;
                $scope.newproject = {};
                $scope.project.error.length = 0;
            })
            .error(function (data, status) {
                $scope.project.state = "error";
                $scope.project.error = data;
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
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
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
                $scope.newcontrib.content = data;
                $scope.contribution = {
                    "author": "",
                    "mail": "",
                    "amount": "",
                    "projectId": $routeParams.projectId
                };
                cotizeProjectService.loadProject($routeParams.projectId)
                                    .success(function (data) { $scope.project.content = data; })
                                    .error(function (data, status) {});
                $scope.newcontrib.errors.splice(0, $scope.newcontrib.errors.length);
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.newcontrib.errors = data;
            });
    };
}]);

cotizeControllers.controller('cotizeContribution', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.contribution = {
    };
    $scope.contrib = {
    };
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.newcontrib = {}
    $scope.loadingdata = {}

    cotizeProjectService.loadProject($routeParams.projectId)
            .success(function (data) { $scope.project.content = data; })
            .error(function (data, status) {
                $scope.loadingdata.error = true;
                $scope.project.status = status;
            });

    cotizeProjectService.loadContribution($routeParams.projectId, $routeParams.contributionId)
            .success(function (data) { $scope.contribution = data; })
            .error(function (data, status) {
                $scope.loadingdata.error = true;
                $scope.project.status = status;
            });

    $scope.contrib.update = function () {
        cotizeProjectService.updateContribution($routeParams.projectId, $routeParams.contributionId, $scope.contribution)
            .success(function (data) {
                $scope.newcontrib.state = "updated";
                $scope.newcontrib.content = data;
                cotizeProjectService.loadProject($routeParams.projectId)
                                    .success(function (data) { $scope.project.content = data; })
                                    .error(function (data, status) { });
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);

cotizeControllers.controller('cotizeAdmin', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.project = {}
    $scope.create = {}
    $scope.contrib = {}
    $scope.event = {
        users : new Set(),
        usersTab : [],
        errors : [],
        messages : []
    }
    $scope.del = {
        contribIndex : -1
    }

    //FIXME beurk... (if you find how to apply an ngRepeate on Set...)
    $scope.event.users.toArray = function () {
        usersTab = [];
        $scope.event.users.forEach(function(value) {
            usersTab.push(value);
        });
        return usersTab;
    };

    cotizeProjectService.loadProjectAdmin($routeParams.projectId, $routeParams.passAdmin)
            .success(function (data) {
                $scope.project.content = data;
                $scope.project.rest = $scope.project.content.contributions
                  .filter(function (contrib) { return !contrib.payed; })
                  .map(function (contrib) { return contrib.amount; })
                  .reduce(function(pv, cv) { return pv + cv; }, 0);
            })
            .error(function (data, status) { });

    $scope.contrib.remove = function (contributionIndex) {
        contrib = $scope.project.content.contributions[contributionIndex]
        cotizeProjectService.removeContribution($routeParams.projectId, contrib.contributionId)
            .success(function (newProject) {
                $scope.project.content.contributions.splice(contributionIndex, 1);
                $scope.project.content.amount = newProject.amount;
                $scope.del.contribIndex = -1;
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };

    $scope.contrib.payed = function (contributionIndex) {
        contrib = $scope.project.content.contributions[contributionIndex]
        cotizeProjectService.payedContribution($routeParams.projectId, contrib.contributionId)
            .success(function (data) {
                $scope.project.content.contributions[contributionIndex] = data;
                $scope.project.rest = data.payed ? $scope.project.rest - data.amount
                                                 : $scope.project.rest + data.amount;
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };

    $scope.contrib.prepareRemindAll = function () {
        $scope.event.users.clear();
        $scope.project.content.contributions.forEach(function (element, index, array) {
            if (!element.payed) {
                $scope.event.users.add(index);
            }
        });
    };

    $scope.contrib.prepareRemind = function (contributionIndex) {
        if (!$scope.event.users.has(contributionIndex)) {
            $scope.event.users.add(contributionIndex);
        } else {
            $scope.event.users.delete(contributionIndex);
        }
    };

    $scope.contrib.remind = function (contributionsIdx) {
        $scope.event.messages = [];
        $scope.event.errors = [];
        contributionsIdx.forEach(function (idx) {
            cotizeProjectService.remindContribution($routeParams.projectId, $scope.project.content.contributions[idx].contributionId)
                .success(function (data) {
                    $scope.contrib.prepareRemind(idx);
                    $scope.event.messages.push("Vous venez d'envoyer une relance à " + $scope.project.content.contributions[idx].author);
                })
                .error(function (data, status) {
                    $scope.contrib.prepareRemind(idx);
                    $scope.event.errors.push("Erreur lors de l'envoi de la relance à " + $scope.project.content.contributions[idx].author);
                });
        });
    };
}]);

cotizeControllers.controller('cotizeRoot', ['$http', '$scope', 'cotizeProjectService', '$routeParams',
function ($http, $scope, cotizeProjectService, $routeParams) {
    // App session information
    $scope.prefix = {
        url : window.document.URL.split('/')[2],
        scheme : window.document.URL.split(':')[0]
    };
    $scope.projects = {}
    $scope.create = {}
    $scope.del = {
        projectIndex : -1
    }

    cotizeProjectService.allProjects($routeParams.rootSecret)
            .success(function (data) { $scope.projects.content = data; })
            .error(function (data, status) { });

    $scope.projects.remove = function (projectIndex) {
        project = $scope.projects.content[projectIndex]
        cotizeProjectService.removeProject($routeParams.rootSecret, project.identifier)
            .success(function (newProject) {
                $scope.del.projectIndex = -1;
                $scope.projects.content.splice(projectIndex, 1);
            })
            .error(function (data, status) {
                $scope.newcontrib.state = "error";
                $scope.project.status = status;
            });
    };
}]);