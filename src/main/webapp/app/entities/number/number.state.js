(function() {
    'use strict';

    angular
        .module('phonebookApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('number', {
            parent: 'entity',
            url: '/number?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Numbers'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/number/numbers.html',
                    controller: 'NumberController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
            }
        })
        .state('number-detail', {
            parent: 'entity',
            url: '/number/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'Number'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/number/number-detail.html',
                    controller: 'NumberDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                entity: ['$stateParams', 'Number', function($stateParams, Number) {
                    return Number.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'number',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('number-detail.edit', {
            parent: 'number-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/number/number-dialog.html',
                    controller: 'NumberDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Number', function(Number) {
                            return Number.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('number.new', {
            parent: 'number',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/number/number-dialog.html',
                    controller: 'NumberDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                number: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('number', null, { reload: 'number' });
                }, function() {
                    $state.go('number');
                });
            }]
        })
        .state('number.edit', {
            parent: 'number',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/number/number-dialog.html',
                    controller: 'NumberDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Number', function(Number) {
                            return Number.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('number', null, { reload: 'number' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('number.delete', {
            parent: 'number',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/number/number-delete-dialog.html',
                    controller: 'NumberDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Number', function(Number) {
                            return Number.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('number', null, { reload: 'number' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
